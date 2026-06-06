package com.booking.system.v1.service.impl;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.ResourceEditDTO;
import com.booking.system.v1.dto.ResourceRequestDTO;
import com.booking.system.v1.dto.ResourceResponseDTO;
import com.booking.system.v1.entity.*;
import com.booking.system.v1.exception.*;
import com.booking.system.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import com.booking.system.v1.mapper.ResourceMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.booking.system.v1.repository.ReservationRepository;
import com.booking.system.v1.repository.ResourceRepository;
import com.booking.system.v1.service.AuditLogService;


import com.booking.system.v1.service.ResourceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final AuditLogService auditLogService;

    private final UserRepository userRepository;



    @Override
    public Page<ResourceResponseDTO> findAllActive(Pageable pageable) {

        Page<Resource> resources = resourceRepository.findByResourceStatus(ResourceStatus.ACTIVE, pageable);

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("No active rooms available");
        }


        return resources.map(resourceMapper::toResponseDTO);


    }

    @Override
    public Page<ResourceResponseDTO> findAllResources(Pageable pageable) {
        Page<Resource> resources = resourceRepository.findAll(pageable);

        return resources.map(resourceMapper::toResponseDTO);

    }

    @Override
    public ResourceResponseDTO findByResourceId(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exists"));

        return resourceMapper.toResponseDTO(resource);
    }


    // its responsibility is to find available rooms between given time period
    @Override
    public List<ResourceResponseDTO> findAvailableRooms(LocalDateTime start, LocalDateTime end) {

        //if start time is not before end time
        if (!start.isBefore(end)) {
            throw new TimeException("Start time must be before end time");
        }

        //if reservation is in the past
        if (!start.isAfter(LocalDateTime.now())) {


            throw new TimeException("Cannot reserve from past");
        }

        List<Resource> resources = resourceRepository
                .findAvailableResources(start, end, ResourceStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING);


        return resources.stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());

    }


    @Override
    public List<ResourceResponseDTO> findRoomsByLocation(Location location) {

        System.out.println("Looking for location: " + location);

        if (location == null) {
            throw new InvalidLocationException("Location not found");
        }


        List<Resource> resources = resourceRepository
                .findByLocationAndStatus(location, ResourceStatus.ACTIVE);

        System.out.println("Found resources: " + resources.size());

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("Did not find available rooms");
        }

        return resources.stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<ResourceResponseDTO> findRoomsByCapacityGreaterAndActiveStatus(Integer capacity) {


        if (capacity == null) {
            throw new InvalidCapacityException("Capacity cannot be null");
        }

        if (capacity <= 0) {
            throw new InvalidCapacityException("Capacity must be greater than 0");
        }

        List<Resource> resources = resourceRepository
                .findByCapacityGreaterThanEqualAndResourceStatus(capacity, ResourceStatus.ACTIVE);

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("Did not find available rooms");
        }


        return resources.stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());

    }


    @Override
    public List<ResourceResponseDTO> findRoomsByCapacity(Integer capacity) {

        if (capacity == null) {
            throw new InvalidCapacityException("Capacity cannot be null");
        }

        if (capacity <= 0) {
            throw new InvalidCapacityException("Capacity must be greater than 0");
        }

        List<Resource> resources = resourceRepository
                .findByCapacityAndResourceStatus(capacity, ResourceStatus.ACTIVE);

        if (resources.isEmpty()) {
            throw new NoRoomsFoundException("Did not find available rooms");
        }


        return resources.stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<ResourceResponseDTO> findAvailableResourcesByFilters(AvailableResourceFilterDTO dto) {

        if (dto == null) {
            throw new DTONullException("DTO cannot be null");
        }

        if (dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new TimeException("Start time must be before end time");
        }
        if (!dto.getStartTime().isAfter(LocalDateTime.now())) {
            throw new TimeException("Cannot find available resources");
        }


        List<Resource> resources = resourceRepository
                .findAvailableResourcesByFilters(
                        dto.getStartTime(),
                        dto.getEndTime(),
                        dto.getLocation(),
                        dto.getCapacity(),
                        ResourceStatus.ACTIVE,
                        ReservationStatus.CONFIRMED,
                        ReservationStatus.PENDING);


        return resources.stream()
                .map(resourceMapper::toResponseDTO)
                .collect(Collectors.toList());


    }

    @Override
    public ResourceResponseDTO create(ResourceRequestDTO dto) {



        if (dto.getRoomNumber() == null) {
            throw new InvalidRoomNumberException("Room number is required");
        }

        if (dto.getRoomNumber() < 1 ||
                dto.getRoomNumber() > 100) {
            throw new InvalidRoomNumberException(
                    "Room number must be between 1 and 100");
        }

        // checks if it exists by name and roomNumber and Location ++ should also check if
        boolean exists =
                resourceRepository
                        .existsByLocationAndRoomNumber(
                                dto.getLocation(),
                                dto.getRoomNumber());
        if (exists) {
            throw new ResourceExistsException(
                    "Room already exists");
        }
        Resource resource = resourceRepository.save(resourceMapper.toEntity(dto));


        auditLogService.log(AuditAction.RESOURCE_CREATED,
                getAdminUsername(),
                "Resource",
                resource.getId(),
                "Resource was created");
        return resourceMapper.toResponseDTO(resource);


    }

    @Override
    public ResourceResponseDTO edit(Long id, ResourceEditDTO dto) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (dto.getResourceName() != null) resource.setName(dto.getResourceName());
        if (dto.getLocation() != null) resource.setLocation(dto.getLocation());
        if (dto.getCapacity() != null && dto.getCapacity() <= 0) {
            throw new InvalidCapacityException("Capacity must be greater than 0");
        }
        if (dto.getCapacity() != null) {
            resource.setCapacity(dto.getCapacity());
        }
        Resource saved = resourceRepository.save(resource);

        auditLogService.log(AuditAction.RESOURCE_UPDATED,
                getAdminUsername(),
                "Resource",
                saved.getId(),
                "Resource was updated");

        return resourceMapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {

        Resource resource = resourceRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        cancelAllByResourceId(id);


        resource.setResourceStatus(ResourceStatus.DELETED);
        resourceRepository.save(resource);

        auditLogService.log(AuditAction.RESOURCE_DELETED,
                getAdminUsername(),
                "Resource",
                resource.getId(),
                "Resource was deleted");


    }

    @Override
    public void activate(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (resource.getResourceStatus() == ResourceStatus.ACTIVE) {
            throw new ActiveException("Resource is already active");
        }

        resource.setResourceStatus(ResourceStatus.ACTIVE);

        resourceRepository.save(resource);

        auditLogService.log(AuditAction.RESOURCE_ACTIVATED,
                getAdminUsername(),
                "Resource",
                resource.getId(),
                "Resource was activated");

    }

    @Override
    public void deactivate(Long id) {
        //if resource is already taken, then we cancel the reservations and then deactivate resource

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (resource.getResourceStatus() == ResourceStatus.DEACTIVATED) {
            throw new DeactivateException("Resource is already deactivated");
        }
        cancelAllByResourceId(id);

        resource.setResourceStatus(ResourceStatus.DEACTIVATED);
        resourceRepository.save(resource);

        auditLogService.log(
                AuditAction.RESOURCE_DEACTIVATED,
                getAdminUsername(),
                "Resource",
                resource.getId(),
                "Resource " + resource.getName() + " was deactivated"
        );


        resource.setResourceStatus(ResourceStatus.DEACTIVATED);

        resourceRepository.save(resource);

    }


    @Override
    public void cancelAllByResourceId(Long resourceId) {

        List<Reservation> reservations = reservationRepository.findByResource_Id(resourceId);


        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus() == ReservationStatus.CONFIRMED) {
                reservation.setReservationStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
                auditLogService.log(
                        AuditAction.RESERVATION_CANCELLED,
                        getAdminUsername(),
                        "Reservation",
                        reservation.getId(),
                        "Reservation cancelled due to resource unavailability"
                );
            }
        }
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
