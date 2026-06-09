package com.booking.system.v1.controller;


import com.booking.system.v1.dto.*;

import com.booking.system.v1.service.ResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.booking.system.v1.service.AdminService;



@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final ResourceService resourceService;

    // ─── USER MANAGEMENT ───────────────────────────────────────

    // GET /api/admin/users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminService.findAllUsers(pageable));
    }

    // GET /api/admin/users/{id}
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findUserById(
            @PathVariable Long id) {

        UserResponseDTO response = adminService.findById(id);
        return ResponseEntity.ok(response);
    }

    // GET /api/admin/users/search/username?username=
    @GetMapping("/users/search/username")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findByUsername(
            @RequestParam String username) {
        return ResponseEntity.ok(adminService.findByUsername(username));
    }

    // GET /api/admin/users/search/email?email=john@example.com
    @GetMapping("/users/search/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> findByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(adminService.findByEmail(email));
    }


    // PUT /api/admin/users/{id}/block
    @PutMapping("/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(@PathVariable Long id) {

        adminService.blockUser(id);
        return ResponseEntity.ok().build();
    }

    // PUT /api/admin/users/{id}/activate
    @PutMapping("/users/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {

        adminService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ─── RESOURCE MANAGEMENT ───────────────────────────────────

    // GET /api/admin/resources
    @GetMapping("/resources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ResourceResponseDTO>> findAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(resourceService.findAllResources(pageable));
    }

    // POST /api/admin/resources
    @PostMapping("/resources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponseDTO> createResource(
            @RequestBody @Valid ResourceRequestDTO dto) {

        ResourceResponseDTO response = adminService.createResource(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/admin/resources/{id}
    @PutMapping("/resources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponseDTO> editResource(
            @PathVariable Long id,
            @RequestBody @Valid ResourceEditDTO dto) {

        ResourceResponseDTO response = adminService.editResource(id, dto);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/admin/resources/{id}
    @DeleteMapping("/resources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {

        adminService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/admin/resources/{id}/activate
    @PutMapping("/resources/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateResource(@PathVariable Long id) {

        adminService.activateResource(id);
        return ResponseEntity.ok().build();
    }

    // PUT /api/admin/resources/{id}/deactivate
    @PutMapping("/resources/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateResource(@PathVariable Long id) {

        adminService.deactivateResource(id);
        return ResponseEntity.noContent().build();
    }

    // ─── RESERVATION MANAGEMENT ────────────────────────────────

    // GET /api/admin/reservations
    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservationResponseDTO>> findAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

    Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page,size,sort);


        return ResponseEntity.ok(adminService.findAllReservations(pageable));
    }

    // GET /api/admin/reservations/{id}
    @GetMapping("/reservations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseDTO> findReservationById(
            @PathVariable Long id) {

        ReservationResponseDTO response = adminService.findReservationById(id);
        return ResponseEntity.ok(response);
    }

    // PUT /api/admin/reservations/{id}/confirm
    @PutMapping("/reservations/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long id) {

        adminService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    // PUT /api/admin/reservations/{id}/cancel
    @PutMapping("/reservations/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {

        adminService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    // ─── AUDIT LOGS ────────────────────────────────────────────

    // GET /api/admin/audit-logs
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> findAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "targetId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page,size,sort);

        return ResponseEntity.ok(adminService.findAllAuditLogs(pageable));
    }

    // GET /api/admin/audit-logs/{id}
    @GetMapping("/audit-logs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponseDTO> findAuditLogById(
            @PathVariable Long id) {

        AuditLogResponseDTO response = adminService.findAuditLogById(id);
        return ResponseEntity.ok(response);
    }

    // GET /api/admin/audit-logs/action
    @GetMapping("/audit-logs/action")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> findAuditLogsByAction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "action") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =  PageRequest.of(page,size,sort);

        return ResponseEntity.ok(adminService.findAllAuditLogs(pageable));
    }
}