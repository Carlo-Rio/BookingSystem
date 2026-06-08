package com.booking.system.v1.controller;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.RescheduleReservationDTO;
import com.booking.system.v1.dto.ReservationResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.booking.system.v1.service.ReservationService;


import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;


    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponseDTO> reserveResource(
            @Valid @RequestBody AvailableResourceFilterDTO dto,
            @Parameter(hidden = true) Authentication authentication
    ) {

        String email = authentication.getName();
        ReservationResponseDTO response =
                reservationService.reserveResource(email, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<ReservationResponseDTO>> getMyReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Authentication authentication) {

        String email = authentication.getName();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(reservationService.viewMyReservations(email, pageable));
    }


    @PatchMapping("/{reservationId}/reschedule")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReservationResponseDTO> rescheduleReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody RescheduleReservationDTO dto,
            @Parameter(hidden = true) Authentication authentication
    ) {


        ReservationResponseDTO response =
                reservationService.rescheduleReservation(
                        reservationId,
                        dto
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam String email,
            @Parameter(hidden = true) Authentication authentication
    ) {


        reservationService.cancelReservation(
                reservationId,
                email
        );

        return ResponseEntity.noContent().build();
    }


}


