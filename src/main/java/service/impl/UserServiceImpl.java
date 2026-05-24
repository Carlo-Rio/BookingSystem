package service.impl;

import dto.*;
import entity.*;
import exception.*;
import lombok.RequiredArgsConstructor;
import mapper.ReservationMapper;
import mapper.ResourceMapper;
import mapper.UserMapper;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.ReservationRepository;
import repository.ResourceRepository;
import repository.UserRepository;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;

    @Override
    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("This email already exists.");
        }

        User saved = userRepository.save(userMapper.toEntity(dto, passwordEncoder));
        return userMapper.toResponseDTO(saved);


    }

    @Override
    public UserResponseDTO findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        return userMapper.toResponseDTO(user);


    }

    @Override
    public UserResponseDTO findByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponseDTO(user);

    }

    @Override
    public UserResponseDTO findByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO editProfile(Long id, UserUpdateDTO dto) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with that id not found"));

        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());


        User saved = userRepository.save(user);


        return userMapper.toResponseDTO(saved);
    }

    @Override
    public void changePassword(Long id, ChangePasswordDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // checks if user types correctly his password, then it will allow him to change password
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");

        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);


    }

    @Override
    public void deleteAccount(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public ReservationResponseDTO makeReservation(Long userId, ReservationRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getStatus().equals(UserStatus.PENDING) || user.getStatus().equals(UserStatus.BLOCKED)) {
            throw new UserNotActiveException("User is not active");
        }

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));


        //czy resourceStatus jest available
        if (resource.getResourceStatus() != ResourceStatus.ACTIVE) {
            throw new ResourceNotAvailableException("Resource is not available");
        }
        // warunek rezerwacji czasu, czy 12 jest przed 14 i czy ktoś nie rezerwuje z przeszłości
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {


            throw new TimeException("endTime cannot be before startTime");


        }
        if (!dto.getStartTime().isAfter(LocalDateTime.now())) {
            throw new TimeException("Start time cannot be in the past");
        }
        //overlapping check
        if (reservationRepository.existsOverlappingReservation(
                dto.getResourceId(),
                dto.getStartTime(),
                dto.getEndTime(),
                ReservationStatus.CONFIRMED)) {
            throw new OverlappingReservationException("Chosen time slot is already booked. Please select another one");
        }


        Reservation reservation = reservationRepository.save(reservationMapper.toEntity(dto, user, resource));

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(AuditAction.RESERVATION_CREATED);
        auditLog.setPerformedBy(user.getUsername());
        auditLog.setTargetEntity("Reservation");
        auditLog.setTargetId(reservation.getId());
        auditLog.setDescription("Reservation created by " + user.getUsername() + " for room " + resource.getName());

        return reservationMapper.toResponseDTO(reservation);


    }

    @Override
    public List<ReservationResponseDTO> viewMyReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new UserNotActiveException("User not active");
        }

        List<Reservation> reservations = reservationRepository.findByUserId(userId);

        List<ReservationResponseDTO> response = reservations.stream()
                .map(reservationMapper::toResponseDTO)
                .collect(Collectors.toList());

        return response;
    }

    // ma za zadanie sprawdzić czy są dostępne pokoje w danym przedziale czasowym
    @Override
    public List<ResourceResponseDTO> findAvailableResource(LocalDateTime start, LocalDateTime end) {

        if (!start.isBefore(end)) {
            throw new TimeException("End cannot be before start");
        }
        // warunek rezerwacji czasu, czy 12 jest przed 14 i czy ktoś nie rezerwuje z przeszłości
        if (!start.isAfter(LocalDateTime.now())) {


            throw new TimeException("Cannot see in the past");
        }

        List<Resource> resources = resourceRepository
                .findAvailableResources(start, end, ResourceStatus.ACTIVE, ReservationStatus.CONFIRMED);

        List<ResourceResponseDTO> response = resources
                .stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());
        return response;

    }


    @Override
    public List<ResourceResponseDTO> findRoomsByLocation(Location location) {

        if (location == null) {
            throw new InvalidLocationException("Location not found");
        }


        List<Resource> resources = resourceRepository
                .findByLocationAndStatus(location, ResourceStatus.ACTIVE);

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("Did not find available rooms");
        }
        List<ResourceResponseDTO> response = resources
                .stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());
        return response;

    }

    @Override
    public List<ResourceResponseDTO> findRoomsByCapacityGreaterThanEqual(Integer capacity) {

        if (capacity == null) {
            throw new InvalidCapacityException("Capacity cannot be null");
        }

        if (capacity <= 0) {
            throw new InvalidCapacityException("Capacity must be greater than 0");
        }

        List<Resource> resources = resourceRepository
                .findByCapacityGreaterThanEqualAndResourceStatus(capacity, ResourceStatus.ACTIVE);

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("Did not find available rooms");
        }
        List<ResourceResponseDTO> response = resources
                .stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());
        return response;

    }
}
