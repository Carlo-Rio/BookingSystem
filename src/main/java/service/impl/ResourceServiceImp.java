package service.impl;

import dto.ResourceRequestDTO;
import dto.ResourceResponseDTO;
import entity.Resource;
import service.ResourceService;

import java.time.LocalDateTime;
import java.util.List;

public class ResourceServiceImp implements ResourceService {


    @Override
    public List<ResourceResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public ResourceResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public List<ResourceResponseDTO> findByLocation(String location) {
        return List.of();
    }

    @Override
    public List<ResourceResponseDTO> findByCapacity(int capacity) {
        return List.of();
    }

    @Override
    public List<ResourceResponseDTO> findAvailable(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    @Override
    public ResourceResponseDTO create(ResourceRequestDTO dto) {
        return null;
    }

    @Override
    public ResourceResponseDTO edit(Long id, ResourceRequestDTO dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void activate(Long id) {

    }

    @Override
    public void deactivate(Long id) {

    }
}
