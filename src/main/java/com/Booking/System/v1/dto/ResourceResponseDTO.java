package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Location;
import com.booking.system.v1.entity.ResourceStatus;
import lombok.Data;

@Data
public class ResourceResponseDTO {


    private Long resourceId;

    private String resourceName;

    private Location location;

    private Integer roomNumber;

    private int capacity;

    private ResourceStatus status;

    private String roomCode;
}
