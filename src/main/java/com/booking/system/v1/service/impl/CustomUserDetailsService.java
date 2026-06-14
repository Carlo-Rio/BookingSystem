package com.booking.system.v1.service.impl;

import com.booking.system.v1.entity.User;
import com.booking.system.v1.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.booking.system.v1.repository.UserRepository;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        System.out.println("Attempting login with email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new LockedException("Account is blocked");
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new DisabledException("Account is not activated yet");
        }

        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new DisabledException("Account is deactivated");
        }
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("User has no role assigned");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}