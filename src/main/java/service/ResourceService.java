package service;

import dto.ResourceRequestDTO;
import dto.ResourceResponseDTO;
import entity.Location;
import entity.Resource;
import entity.ResourceStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceService {

    List<ResourceResponseDTO> findAll();

    ResourceResponseDTO findById(Long id);

    List<ResourceResponseDTO> findByLocation(Location location);

    List<ResourceResponseDTO> findByCapacity(int capacity);

    List<ResourceResponseDTO> findAvailable(LocalDateTime start, LocalDateTime end);

    ResourceResponseDTO create(ResourceRequestDTO dto);

    ResourceResponseDTO edit(Long id, ResourceRequestDTO dto);

    void delete(Long id);

    void activate(Long id);

    void deactivate(Long id);

}
