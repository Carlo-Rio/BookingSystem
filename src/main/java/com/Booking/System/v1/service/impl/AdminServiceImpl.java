package com.booking.system.v1.service.impl;

import com.booking.system.v1.dto.*;
import com.booking.system.v1.entity.*;

import com.booking.system.v1.exception.*;
import com.booking.system.v1.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.booking.system.v1.repository.ReservationRepository;

import com.booking.system.v1.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ResourceService resourceService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;


    @Override
    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @Override
    public UserResponseDTO findById(Long id) {
        return userService.findById(id, getAdminUsername());
    }

    @Override
    public UserResponseDTO findByUsername(String username) {
        return userService.findByUsername(username);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        return userService.findByEmail(email);
    }

    @Override
    public void blockUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("User is already blocked");
        }

        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        auditLogService
                .log(AuditAction.USER_BLOCKED,
                        getAdminUsername(),
                        "User",
                        user.getId(),
                        "User " + user.getUsername() + " was blocked");


    }

    @Override
    public void activateUser(Long id) {


        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserActiveException("User is already active");
        }


        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        auditLogService.log(
                AuditAction.USER_ACTIVATED,
                getAdminUsername(),
                "User",
                user.getId(),
                "User " + user.getUsername() + " was activated"
        );


    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // add this check
        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new UserAlreadyDeletedException("User is already deleted");
        }

        List<Reservation> reservations = reservationRepository.findByUser_Id(id);
        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
                reservation.setReservationStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
            }
            auditLogService.log(
                    AuditAction.RESERVATION_CANCELLED,
                    getAdminUsername(),
                    "Reservation",
                    reservation.getId(),
                    "Reservation cancelled due to user deletion"
            );

        }
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        auditLogService.log(
                AuditAction.USER_DELETED,
                getAdminUsername(),
                "User",
                user.getId(),
                "User " + user.getUsername() + " was deleted"
        );

    }

    @Override
    public ResourceResponseDTO createResource(ResourceRequestDTO dto) {
        return resourceService.create(dto);
    }

    @Override
    public ResourceResponseDTO editResource(Long id, ResourceEditDTO dto) {
        return resourceService.edit(id, dto);
    }





    @Override
    public void activateResource(Long id) {
        resourceService.activate(id);

    }

    @Override
    public void deleteResource(Long id) {
        resourceService.delete(id);
    }

    @Override
    public void deactivateResource(Long id) {
        resourceService.deactivate(id);
    }

    @Override
    public Page<ReservationResponseDTO> findAllReservations(Pageable pageable) {

        return reservationService.findAll(pageable);
    }

    @Override
    public ReservationResponseDTO findReservationById(Long id) {
        return reservationService.findById(id);


    }

    @Override
    public void confirmReservation(Long id) {
        reservationService.confirm(id);

    }

    @Override
    public void cancelReservation(Long id) {
        reservationService.cancel(id);
    }

    @Override
    public Page<AuditLogResponseDTO> findAllAuditLogs(Pageable pageable) {

        return auditLogService.findAll(pageable);
    }

    @Override
    public AuditLogResponseDTO findAuditLogById(Long id) {
        return auditLogService.findByAuditId(id);
    }





    private String getAdminUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new UnauthorizedException("No authenticated user found");

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(User::getUsername)
                .orElse(email); // fallback to email if user not found
    }
}
