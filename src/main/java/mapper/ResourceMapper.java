package mapper;

import dto.ResourceRequestDTO;
import dto.ResourceResponseDTO;
import entity.Resource;
import entity.User;

public class ResourceMapper {

    public Resource toEntity(ResourceRequestDTO dto) {

        Resource resource = new Resource();

        resource.setName(dto.getResourceName());
        resource.setLocation(dto.getLocation());
        resource.setCapacity(dto.getCapacity());

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

        return dto;
    }


}
