package service;

import dto.*;
import entity.Location;
import entity.ResourceStatus;
import entity.User;

import java.time.LocalDateTime;
import java.util.List;


// użytkownik powinien: wyszukać resource, zarezerwować resource, reschedulować rezerwacje
// uniknąć duplikowania funkcji np. cancelReservation; cancelReservation będzie delegowane do interfejsu ReservationService
public interface UserService {

    UserResponseDTO register(UserRegistrationDTO dto);

    UserResponseDTO findById(Long id);

    UserResponseDTO findByEmail(String email);

    UserResponseDTO findByUsername(String username);


    UserResponseDTO editProfile(Long id, UserUpdateDTO dto);

    void changePassword(Long id, ChangePasswordDTO dto);

    void deleteAccount(Long id);


    ReservationResponseDTO makeReservation(Long userId, ReservationRequestDTO dto);

    List<ReservationResponseDTO> viewMyReservations(Long userId);

    List<ResourceResponseDTO> findAvailableResource(LocalDateTime start, LocalDateTime end);

    List<ResourceResponseDTO> findRoomsByLocation(Location location);

    List<ResourceResponseDTO> findRoomsByCapacityGreaterThanEqual(Integer capacity);


}
