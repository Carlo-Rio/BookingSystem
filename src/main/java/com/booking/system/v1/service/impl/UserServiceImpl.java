package com.booking.system.v1.service.impl;

import com.booking.system.v1.dto.*;
import com.booking.system.v1.entity.Role;
import com.booking.system.v1.entity.User;
import com.booking.system.v1.entity.UserStatus;
import com.booking.system.v1.exception.*;
import lombok.RequiredArgsConstructor;


import com.booking.system.v1.mapper.UserMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.UserService;




@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;



    @Override
    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("This email already exists");
        }

        User saved = userRepository.save(userMapper.toEntity(dto, passwordEncoder));
        return userMapper.toResponseDTO(saved);


    }

    @Override
    public UserResponseDTO findById(Long id, String loggedInEmail) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        // allow admin to see anyone, restrict user to own profile
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (loggedInUser.getRole() != Role.ADMIN &&
                !loggedInUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only view your own profile");
        }


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
    public UserResponseDTO editProfile(String email, UserUpdateDTO dto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());

        User saved = userRepository.save(user);
        return userMapper.toResponseDTO(saved);
    }

    @Override
    public void changePassword(String email, ChangePasswordDTO dto) {

        User user = userRepository.findByEmail(email)
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
    public void deleteAccount(String loggedInEmail) {
        User user = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // users can only delete their own account
        if (!loggedInUser.getEmail().equals(loggedInEmail)) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new UserAlreadyDeletedException("Account is already deleted");
        }

        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }




    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable) {

        Page<User> user = userRepository.findAll(pageable);

        return user.map(userMapper::toResponseDTO);


    }
}
