package com.booking.system.v1.service;

import com.booking.system.v1.dto.AvailableResourceFilterDTO;
import com.booking.system.v1.dto.ResourceEditDTO;
import com.booking.system.v1.dto.ResourceRequestDTO;
import com.booking.system.v1.dto.ResourceResponseDTO;
import com.booking.system.v1.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceService {

    Page<ResourceResponseDTO> findAllActive(Pageable pageable);

    Page<ResourceResponseDTO> findAllResources(Pageable pageable);

    ResourceResponseDTO findByResourceId(Long id);

    List<ResourceResponseDTO> findAvailableRooms(LocalDateTime start, LocalDateTime end);

    List<ResourceResponseDTO> findRoomsByLocation(Location location);

    List<ResourceResponseDTO> findRoomsByCapacity(Integer capacity);

    List<ResourceResponseDTO> findRoomsByCapacityGreaterAndActiveStatus(Integer capacity);

    List<ResourceResponseDTO> findAvailableResourcesByFilters(AvailableResourceFilterDTO dto);
    ResourceResponseDTO create(ResourceRequestDTO dto);

    ResourceResponseDTO edit(Long id, ResourceEditDTO dto);

    void delete(Long id);

    void activate(Long id);

    void deactivate(Long id);

    void cancelAllByResourceId(Long resourceId);


}
