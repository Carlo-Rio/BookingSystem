package com.booking.system.v1.controller;

import com.booking.system.v1.configuration.JwtService;
import com.booking.system.v1.configuration.TokenBlacklist;
import com.booking.system.v1.dto.LoginRequestDTO;
import com.booking.system.v1.dto.LoginResponseDTO;
import com.booking.system.v1.entity.User;
import com.booking.system.v1.exception.UserNotFoundException;
import com.booking.system.v1.repository.UserRepository;
import com.booking.system.v1.service.impl.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklist tokenBlacklist;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );


        UserDetails userDetails = userDetailsService
                .loadUserByUsername(dto.getEmail());

        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        user.setCurrentToken(token);
        userRepository.save(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklist.blacklist(token);

            // clear current token from user
            String email = jwtService.extractEmail(token);
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setCurrentToken(null);
                userRepository.save(user);
            });
        }

        return ResponseEntity.ok("{\"message\": \"Logout successful\"}");
    }

}