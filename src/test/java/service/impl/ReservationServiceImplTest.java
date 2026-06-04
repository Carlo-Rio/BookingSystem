package service.impl;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.ReservationResponseDTO;
import com.booking.system.v1.entity.*;
import com.booking.system.v1.exception.NoRoomsAvailableException;
import com.booking.system.v1.exception.TimeException;
import com.booking.system.v1.exception.UserNotActiveException;
import com.booking.system.v1.exception.UserNotFoundException;
import com.booking.system.v1.mapper.ReservationMapper;
import com.booking.system.v1.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.repository.ResourceRepository;
import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.AuditLogService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ReservationServiceImpl reservationService;



    private AvailableResourceFilterDTO buildValidFilter() {
        AvailableResourceFilterDTO dto = new AvailableResourceFilterDTO();
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(3));
        dto.setLocation(Location.FLOOR_1);
        dto.setCapacity(5);
        return dto;
    }

    private User buildActiveUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private Resource buildActiveResource() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Room A");
        resource.setCapacity(10);
        resource.setResourceStatus(ResourceStatus.ACTIVE);
        return resource;
    }

    @Test
    void reserveResource_shouldReturnResponseDTO_whenAllConditionsAreMet() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();
        User user = buildActiveUser();
        Resource resource = buildActiveResource();

        Reservation savedReservation = new Reservation();
        savedReservation.setId(1L);

        ReservationResponseDTO expectedResponse = new ReservationResponseDTO();
        expectedResponse.setResourceName("Room A");

        // add this to both failing reservation tests
        when(resourceRepository.findByIdWithLock(any()))
                .thenReturn(Optional.of(resource));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(resourceRepository.findAvailableResourcesByFilters(
                any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(resource));
        when(reservationMapper.toEntity(any(), any(), any())).thenReturn(savedReservation);
        when(reservationRepository.save(any())).thenReturn(savedReservation);
        when(reservationMapper.toResponseDTO(any())).thenReturn(expectedResponse);

        // Act
        ReservationResponseDTO result = reservationService.reserveResource(email, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Room A", result.getResourceName());
        verify(reservationRepository).save(any());
        verify(auditLogService).log(any(), any(), any(), any(), any());
    }

    @Test
    void reserveResource_shouldThrowTimeException_whenStartTimeIsAfterEndTime() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();
        dto.setStartTime(LocalDateTime.now().plusHours(3));
        dto.setEndTime(LocalDateTime.now().plusHours(1));

        // Act and Assert
        assertThrows(TimeException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(userRepository, never()).findById(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveResource_shouldThrowTimeException_whenStartTimeIsInThePast() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();
        dto.setStartTime(LocalDateTime.now().minusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(1));

        // Act and Assert
        assertThrows(TimeException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(userRepository, never()).findById(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveResource_shouldThrowUserNotFoundException_whenUserDoesNotExist() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(resourceRepository, never()).findAvailableResourcesByFilters(
                any(), any(), any(), any(), any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveResource_shouldThrowUserNotActiveException_whenUserIsBlocked() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();

        User blockedUser = buildActiveUser();
        blockedUser.setStatus(UserStatus.BLOCKED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(blockedUser));

        // Act and Assert
        assertThrows(UserNotActiveException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(resourceRepository, never()).findAvailableResourcesByFilters(
                any(), any(), any(), any(), any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveResource_shouldThrowUserNotActiveException_whenUserIsPending() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();

        User pendingUser = buildActiveUser();
        pendingUser.setStatus(UserStatus.PENDING);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(pendingUser));

        // Act and Assert
        assertThrows(UserNotActiveException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveResource_shouldThrowNoRoomsAvailableException_whenNoRoomsMatchFilters() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();
        User user = buildActiveUser();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(resourceRepository.findAvailableResourcesByFilters(
                any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(NoRoomsAvailableException.class,
                () -> reservationService.reserveResource(email, dto));

        verify(reservationRepository, never()).save(any());
        verify(auditLogService, never()).log(any(), any(), any(), any(), any());
    }

    @Test
    void reserveResource_shouldSelectSmallestAvailableRoom() {

        // Arrange
        String email = "sss@gmail.com";
        AvailableResourceFilterDTO dto = buildValidFilter();
        User user = buildActiveUser();

        Resource smallRoom = new Resource();
        smallRoom.setId(1L);
        smallRoom.setName("Small Room");
        smallRoom.setCapacity(6);
        smallRoom.setResourceStatus(ResourceStatus.ACTIVE);

        Resource largeRoom = new Resource();
        largeRoom.setId(2L);
        largeRoom.setName("Large Room");
        largeRoom.setCapacity(20);
        largeRoom.setResourceStatus(ResourceStatus.ACTIVE);

        Reservation savedReservation = new Reservation();
        ReservationResponseDTO expectedResponse = new ReservationResponseDTO();
        expectedResponse.setResourceName("Small Room");


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(resourceRepository.findAvailableResourcesByFilters(
                any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(largeRoom, smallRoom));

// ADD THIS — mock the pessimistic lock call
        when(resourceRepository.findByIdWithLock(any()))
                .thenReturn(Optional.of(smallRoom));

        when(reservationRepository.existsOverlappingReservation(
                any(), any(), any(), any(), any()))
                .thenReturn(false);
        when(reservationMapper.toEntity(any(), any(), any())).thenReturn(savedReservation);
        when(reservationRepository.save(any())).thenReturn(savedReservation);
        when(reservationMapper.toResponseDTO(any())).thenReturn(expectedResponse);

        // Act
        ReservationResponseDTO result = reservationService.reserveResource(email, dto);

        // Assert
        assertEquals("Small Room", result.getResourceName());
    }


}