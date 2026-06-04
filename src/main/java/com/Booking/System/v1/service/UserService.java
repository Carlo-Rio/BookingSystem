package com.booking.system.v1.service;

import com.booking.system.v1.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


// użytkownik powinien: wyszukać resource, zarezerwować resource, reschedulować rezerwacje
// uniknąć duplikowania funkcji np. cancelReservation; cancelReservation będzie delegowane do interfejsu ReservationService
public interface UserService {

    Page<UserResponseDTO> findAll(Pageable pageable);

    UserResponseDTO register(UserRegistrationDTO dto);


    UserResponseDTO findById(Long id, String loggedInEmail);

    UserResponseDTO findByEmail(String email);

    UserResponseDTO findByUsername(String username);


    UserResponseDTO editProfile(Long id, UserUpdateDTO dto, String loggedInEmail);

    void changePassword(Long id, ChangePasswordDTO dto);

    void deleteAccount(Long id, String email);







}
