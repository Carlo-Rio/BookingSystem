package com.booking.system.v1.controller;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;

import com.booking.system.v1.dto.ResourceResponseDTO;
import com.booking.system.v1.entity.Location;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.booking.system.v1.service.ResourceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // GET /api/resources
    // user views all active resources
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<ResourceResponseDTO>> findAllActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

       Sort sort = direction.equalsIgnoreCase("desc")
               ? Sort.by(sortBy).descending()
               : Sort.by(sortBy).ascending();


       Pageable pageable = PageRequest.of(page,size,sort);
       return ResponseEntity.ok(resourceService.findAllActive(pageable));
    }



    // GET /api/resources/{id}
    // user views a specific resource
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ResourceResponseDTO> findById(
            @PathVariable Long id) {

        ResourceResponseDTO response = resourceService.findByResourceId(id);
        return ResponseEntity.ok(response);
    }

    // GET /api/resources/available
    // user views available resources for a time slot
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ResourceResponseDTO>> findAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        List<ResourceResponseDTO> response =
                resourceService.findAvailableRooms(start, end);
        return ResponseEntity.ok(response);
    }

    // GET /api/resources/location
    // user filters resources by location
    @GetMapping("/location")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ResourceResponseDTO>> findByLocation(
            @RequestParam Location location) {

        List<ResourceResponseDTO> response =
                resourceService.findRoomsByLocation(location);
        return ResponseEntity.ok(response);
    }

    // GET /api/resources/capacity
    // user filters resources by minimum capacity


    // exact capacity
    @GetMapping("/capacity/exact")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ResourceResponseDTO>> findByExactCapacity(
            @RequestParam Integer capacity) {
        return ResponseEntity.ok(resourceService.findRoomsByCapacity(capacity));
    }

    // capacity greater than or equal
    @GetMapping("/capacity")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ResourceResponseDTO>> findByCapacity(
            @RequestParam Integer capacity) {
        return ResponseEntity.ok(
                resourceService.findRoomsByCapacityGreaterAndActiveStatus(capacity));
    }

    // GET /api/resources/filter
    // user filters resources by all criteria at once
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ResourceResponseDTO>> findByFilters(
            @RequestBody @Valid AvailableResourceFilterDTO dto) {

        List<ResourceResponseDTO> response =
                resourceService.findAvailableResourcesByFilters(dto);
        return ResponseEntity.ok(response);
    }


}