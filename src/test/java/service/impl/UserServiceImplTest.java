package service.impl;

import com.booking.system.v1.dto.UserRegistrationDTO;
import com.booking.system.v1.dto.UserResponseDTO;
import com.booking.system.v1.entity.User;
import com.booking.system.v1.exception.EmailAlreadyExistsException;
import com.booking.system.v1.mapper.ReservationMapper;
import com.booking.system.v1.mapper.ResourceMapper;
import com.booking.system.v1.mapper.UserMapper;
import com.booking.system.v1.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.repository.ResourceRepository;
import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.AuditLogService;
import com.booking.system.v1.service.ReservationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationService reservationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserServiceImpl userService;



    @Test
    void register_shouldReturnUserResponseDTO_whenEmailIsUnique() {

        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername("johndoe");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        User mappedUser = new User();
        mappedUser.setUsername("johndoe");
        mappedUser.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("johndoe");
        savedUser.setEmail("john@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setUsername("johndoe");
        expectedResponse.setEmail("john@example.com");

        // tell mocks what to return
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userMapper.toEntity(any(UserRegistrationDTO.class), any(PasswordEncoder.class)))
                .thenReturn(mappedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(expectedResponse);

        // Act
        UserResponseDTO result = userService.register(dto);

        // Assert
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());

        // verify repository and mapper were actually called
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(mappedUser);
        verify(userMapper).toResponseDTO(savedUser);
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("john@example.com");

        when(userRepository.existsByEmail(any())).thenReturn(true);

        // Act and Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(dto));

        // verify save was never called since email already exists
        verify(userRepository, never()).save(any());
    }


}
