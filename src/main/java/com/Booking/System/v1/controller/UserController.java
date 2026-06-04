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
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(
            @PathVariable Long id,
            @Parameter(hidden = true)Authentication authentication) {

        String loggedInEmail = authentication.getName();
        UserResponseDTO response = userService.findById(id, loggedInEmail);
        return ResponseEntity.ok(response);
    }




    // PUT /api/users/{id}/profile
    // user edits their own profile
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDTO> editProfile(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO dto,
            @Parameter(hidden = true)Authentication authentication) {

        UserResponseDTO response = userService.editProfile(id, dto,  authentication.getName() );
        return ResponseEntity.ok(response);
    }

    // PUT /api/users/{id}/password
    // user changes their own password
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody @Valid ChangePasswordDTO dto) {

        userService.changePassword(id, dto);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/users/{id}
    // user deletes their own account
    // should mark it as deleted, but admin should hard delete and user should softly delete it
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id,
            @Parameter(hidden = true)Authentication authentication,
            HttpServletRequest request) {

        // verify user is deleting their own account
        String loggedInEmail = authentication.getName();
        userService.deleteAccount(id, loggedInEmail);

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