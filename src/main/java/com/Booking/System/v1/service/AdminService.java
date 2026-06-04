package com.booking.system.v1.service;

import com.booking.system.v1.dto.*;
import com.booking.system.v1.entity.AuditAction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;




//admin service
//find all users
// search by a specific id
// search by username
// search by email
// activate account
// block account
// delete account
// create resource
// edit resource
// find all resources
// activate resource
// delete resource
// deactivate resource
// find all reservations
// find reservations by id
// confirm reservations
// cancel reservations
// find all audit logs
// find audit log by id
// find audit logs by action


public interface AdminService {

    Page<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findById(Long id);

    UserResponseDTO findByUsername(String username);

    UserResponseDTO findByEmail(String email);

    void activateUser(Long id);

    void blockUser(Long id);

    void deleteUser(Long id);


    ResourceResponseDTO createResource(ResourceRequestDTO dto);

    ResourceResponseDTO editResource(Long id, ResourceEditDTO dto);

    Page<ResourceResponseDTO> findAllResources(Pageable pageable);

    void activateResource(Long id);

    void deleteResource(Long id);

    void deactivateResource(Long id);

    Page<ReservationResponseDTO> findAllReservations(Pageable pageable);

    ReservationResponseDTO findReservationById(Long id);

    void confirmReservation(Long id);

    void cancelReservation(Long id);

    Page<AuditLogResponseDTO> findAllAuditLogs(Pageable pageable);

    AuditLogResponseDTO findAuditLogById(Long id);

    Page<AuditLogResponseDTO> findAuditLogsByActions(AuditAction action, Pageable pageable);


}
