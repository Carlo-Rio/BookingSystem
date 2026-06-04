package com.booking.system.v1.mapper;

import com.booking.system.v1.dto.ResourceRequestDTO;
import com.booking.system.v1.dto.ResourceResponseDTO;
import com.booking.system.v1.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public Resource toEntity(ResourceRequestDTO dto) {

        Resource resource = new Resource();

        resource.setName(dto.getResourceName());
        resource.setLocation(dto.getLocation());
        resource.setCapacity(dto.getCapacity());
        resource.setRoomNumber(dto.getRoomNumber());

        return resource;
    }

    public ResourceResponseDTO toResponseDTO(Resource resource) {

        ResourceResponseDTO dto = new ResourceResponseDTO();

        dto.setResourceId(resource.getId());
        dto.setResourceName(resource.getName());
        dto.setLocation(resource.getLocation());
        dto.setCapacity(resource.getCapacity());
        dto.setStatus(resource.getResourceStatus());
        dto.setRoomNumber(resource.getRoomNumber());
        dto.setRoomCode(resource.getLocation().buildRoomCode(resource.getRoomNumber()));


        return dto;
    }


}
