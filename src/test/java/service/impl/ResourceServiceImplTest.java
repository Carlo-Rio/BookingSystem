package service.impl;

import com.booking.system.v1.dto.ResourceRequestDTO;
import com.booking.system.v1.dto.ResourceResponseDTO;
import com.booking.system.v1.entity.*;
import com.booking.system.v1.exception.*;
import com.booking.system.v1.mapper.ResourceMapper;
import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.repository.ResourceRepository;
import com.booking.system.v1.service.AuditLogService;
import com.booking.system.v1.service.CurrentUserService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;



@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AuditLogService auditLogService;


    @Mock
    private CurrentUserService currentUserService;


    @InjectMocks
    private ResourceServiceImpl resourceService;


    @Mock
    private UserRepository userRepository;




    // ─── helper methods ────────────────────────────────────────

    private Resource buildActiveResource() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Room A");
        resource.setCapacity(10);
        resource.setLocation(Location.FLOOR_1);
        resource.setResourceStatus(ResourceStatus.ACTIVE);
        return resource;
    }

    private ResourceResponseDTO buildResourceResponseDTO() {
        ResourceResponseDTO dto = new ResourceResponseDTO();
        dto.setResourceName("Room A");
        dto.setLocation(Location.FLOOR_1);
        dto.setCapacity(10);
        return dto;
    }

    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ─── findAllActive() ───────────────────────────────────────

    @Test
    void findAllActive_shouldReturnListOfDTOs_whenActiveResourcesExist() {

        // Arrange
        Pageable pageable = PageRequest.of(0,20);
        Resource resource = buildActiveResource();
        ResourceResponseDTO responseDTO = buildResourceResponseDTO();

        Page<Resource> page = new PageImpl<>(List.of(resource), pageable,1);

        when(resourceRepository.findByResourceStatus(ResourceStatus.ACTIVE, pageable))
                .thenReturn(page);
        when(resourceMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<ResourceResponseDTO> result = resourceService.findAllActive(pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
        verify(resourceRepository).findByResourceStatus(ResourceStatus.ACTIVE, pageable);
    }

    @Test
    void findAllActive_shouldThrowNoRoomsFoundException_whenNoActiveRoomsExist() {

        Pageable pageable = PageRequest.of(0, 20);

        Page<Resource> emptyPage =  Page.empty(pageable);

        // Arrange
        when(resourceRepository.findByResourceStatus(ResourceStatus.ACTIVE, pageable))
                .thenReturn(emptyPage);

        // Act and Assert
        assertThrows(NoRoomsFoundException.class,
                () -> resourceService.findAllActive(pageable));

        verify(resourceMapper, never()).toResponseDTO(any());
    }

    // ─── findByResourceId() ────────────────────────────────────

    @Test
    void findByResourceId_shouldReturnDTO_whenResourceExists() {

        // Arrange
        Resource resource = buildActiveResource();
        ResourceResponseDTO responseDTO = buildResourceResponseDTO();

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        ResourceResponseDTO result = resourceService.findByResourceId(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Room A", result.getResourceName());
        verify(resourceRepository).findById(1L);
    }

    @Test
    void findByResourceId_shouldThrowResourceNotFoundException_whenResourceDoesNotExist() {

        // Arrange
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> resourceService.findByResourceId(99L));

        verify(resourceMapper, never()).toResponseDTO(any());
    }

    // ─── create() ──────────────────────────────────────────────

    @Test
    void create_shouldReturnResponseDTO_whenResourceNameIsUnique() {

        mockSecurityContext("admin@bookingsystem.com");

        User adminUser = new User();
        adminUser.setUsername("admin");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(adminUser));

        ResourceRequestDTO dto = new ResourceRequestDTO();
        dto.setResourceName("Room A");
        dto.setLocation(Location.FLOOR_1);
        dto.setCapacity(10);
        dto.setRoomNumber(10);

        Resource mappedResource = buildActiveResource();
        Resource savedResource = buildActiveResource();
        ResourceResponseDTO responseDTO = buildResourceResponseDTO();

        when(resourceRepository.existsByLocationAndRoomNumber(any(),any())).thenReturn(false);
        when(resourceMapper.toEntity(any())).thenReturn(mappedResource);
        when(resourceRepository.save(any())).thenReturn(savedResource);
        when(resourceMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResourceResponseDTO result = resourceService.create(dto);

        assertNotNull(result);
        assertEquals("Room A", result.getResourceName());
        verify(resourceRepository).save(any());
        verify(auditLogService).log(any(), any(), any(), any(), any());
    }

    @Test
    void create_shouldThrowResourceExistsException_whenResourceNameAlreadyExists() {

        // Arrange
        ResourceRequestDTO dto = new ResourceRequestDTO();
        dto.setResourceName("Room A");
        dto.setRoomNumber(10);

        when(resourceRepository.existsByLocationAndRoomNumber(any(),any())).thenReturn(true);

        // Act and Assert
        assertThrows(ResourceExistsException.class,
                () -> resourceService.create(dto));

        verify(resourceRepository, never()).save(any());
        verify(auditLogService, never()).log(any(), any(), any(), any(), any());
    }

    // ─── activate() ────────────────────────────────────────────

    @Test
    void activate_shouldActivateResource_whenResourceIsNotActive() {

        mockSecurityContext("admin@bookingsystem.com");

        User adminUser = new User();
        adminUser.setUsername("admin");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(adminUser));

        Resource resource = buildActiveResource();
        resource.setResourceStatus(ResourceStatus.DEACTIVATED);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        resourceService.activate(1L);

        assertEquals(ResourceStatus.ACTIVE, resource.getResourceStatus());
        verify(resourceRepository).save(resource);
        verify(auditLogService).log(any(), any(), any(), any(), any());
    }

    @Test
    void activate_shouldThrowActiveException_whenResourceIsAlreadyActive() {

        // Arrange
        Resource resource = buildActiveResource();

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        // Act and Assert
        assertThrows(ActiveException.class,
                () -> resourceService.activate(1L));

        verify(resourceRepository, never()).save(any());
        verify(auditLogService, never()).log(any(), any(), any(), any(), any());
    }

    // ─── deactivate() ──────────────────────────────────────────

    @Test
    void deactivate_shouldDeactivateResource_andCancelConfirmedReservations() {

        mockSecurityContext("admin@bookingsystem.com");

        User adminUser = new User();
        adminUser.setUsername("admin");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(adminUser));

        Resource resource = buildActiveResource();

        Reservation confirmedReservation = new Reservation();
        confirmedReservation.setId(1L);
        confirmedReservation.setReservationStatus(ReservationStatus.CONFIRMED);

        Reservation cancelledReservation = new Reservation();
        cancelledReservation.setId(2L);
        cancelledReservation.setReservationStatus(ReservationStatus.CANCELLED);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(reservationRepository.findByResource_Id(1L))
                .thenReturn(List.of(confirmedReservation, cancelledReservation));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        resourceService.deactivate(1L);

        assertEquals(ResourceStatus.DEACTIVATED, resource.getResourceStatus());
        assertEquals(ReservationStatus.CANCELLED,
                confirmedReservation.getReservationStatus());
        verify(resourceRepository, times(2)).save(any());
        verify(auditLogService, times(2)).log(any(), any(), any(), any(), any());
    }

    @Test
    void deactivate_shouldThrowDeactivateException_whenResourceIsAlreadyDeactivated() {

        // Arrange
        Resource resource = buildActiveResource();
        resource.setResourceStatus(ResourceStatus.DEACTIVATED);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        // Act and Assert
        assertThrows(DeactivateException.class,
                () -> resourceService.deactivate(1L));

        verify(resourceRepository, never()).save(any());
        verify(auditLogService, never()).log(any(), any(), any(), any(), any());
    }

    // ─── findRoomsByCapacity() ─────────────────────────────────

    @Test
    void findRoomsByCapacity_shouldReturnRooms_whenValidCapacityProvided() {

        Resource resource = buildActiveResource();
        ResourceResponseDTO responseDTO = buildResourceResponseDTO();

        // CORRECT — mock the exact match method that findRoomsByCapacity actually calls
        when(resourceRepository.findByCapacityAndResourceStatus(
                any(), any())).thenReturn(List.of(resource));
        when(resourceMapper.toResponseDTO(any())).thenReturn(responseDTO);

        List<ResourceResponseDTO> result = resourceService.findRoomsByCapacity(5);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findRoomsByCapacityGreater_shouldReturnRooms_whenValidCapacityProvided() {

        Resource resource = buildActiveResource();
        ResourceResponseDTO responseDTO = buildResourceResponseDTO();

        when(resourceRepository.findByCapacityGreaterThanEqualAndResourceStatus(
                any(), any())).thenReturn(List.of(resource));
        when(resourceMapper.toResponseDTO(any())).thenReturn(responseDTO);

        List<ResourceResponseDTO> result =
                resourceService.findRoomsByCapacityGreaterAndActiveStatus(5);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void findRoomsByCapacity_shouldThrowInvalidCapacityException_whenCapacityIsZero() {

        // Act and Assert
        assertThrows(InvalidCapacityException.class,
                () -> resourceService.findRoomsByCapacity(0));

        verify(resourceRepository, never())
                .findByCapacityGreaterThanEqualAndResourceStatus(any(), any());
    }

    @Test
    void findRoomsByCapacity_shouldThrowInvalidCapacityException_whenCapacityIsNull() {

        // Act and Assert
        assertThrows(InvalidCapacityException.class,
                () -> resourceService.findRoomsByCapacity(null));

        verify(resourceRepository, never())
                .findByCapacityGreaterThanEqualAndResourceStatus(any(), any());
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

}