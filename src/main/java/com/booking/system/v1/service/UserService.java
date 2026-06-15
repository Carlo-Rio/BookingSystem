package com.booking.system.v1.service;

import com.booking.system.v1.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface UserService {

    Page<UserResponseDTO> findAll(Pageable pageable);

    UserResponseDTO register(UserRegistrationDTO dto);


    UserResponseDTO findById(Long id, String loggedInEmail);

    UserResponseDTO findByEmail(String email);

    UserResponseDTO findByUsername(String username);


    UserResponseDTO editProfile(String loggedInEmail, UserUpdateDTO dto);

    void changePassword(String loggedInEmail, ChangePasswordDTO dto);

    void deleteAccount(String email);







}
