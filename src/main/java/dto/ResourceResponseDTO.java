package dto;

import entity.Location;
import entity.ResourceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResourceResponseDTO {


    private Long resourceId;

    private String resourceName;

    private Location location;

    private String roomNumber;

    private int capacity;

    private ResourceStatus status;


}
