package com.booking.system.v1.controller;

import com.booking.system.v1.dto.ChangePasswordDTO;
import com.booking.system.v1.dto.UserRegistrationDTO;
import com.booking.system.v1.dto.UserResponseDTO;
import com.booking.system.v1.dto.UserUpdateDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.booking.system.v1.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users/register
    // public endpoint — no auth required
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody @Valid UserRegistrationDTO dto) {

        UserResponseDTO response = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/users/{id}
    // user views their own profile
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponseDTO> getMyProfile(
            @Parameter(hidden = true)Authentication authentication) {
        String loggedInEmail = authentication.getName();
        UserResponseDTO response = userService.findByEmail(loggedInEmail);
        return ResponseEntity.ok(response);
    }




    // PUT /api/users/{id}/profile
    // user edits their own profile
    @PutMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponseDTO> editMyProfile(
            @RequestBody @Valid UserUpdateDTO dto,
            @Parameter(hidden = true)Authentication authentication) {

        String email = authentication.getName();
        UserResponseDTO response = userService.editProfile(email, dto );
        return ResponseEntity.ok(response);
    }

    // PUT /api/users/{id}/password
    // user changes their own password
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordDTO dto,
            @Parameter(hidden = true) Authentication authentication) {

        String email = authentication.getName();
        userService.changePassword(email, dto);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/users/{id}
    // user deletes their own account
    // should mark it as deleted, but admin should hard delete and user should softly delete it
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) HttpServletRequest request) {

        // verify user is deleting their own account
        String loggedInEmail = authentication.getName();
        userService.deleteAccount(loggedInEmail);

        // invalidate session immediately after deletion
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // clear security context
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}