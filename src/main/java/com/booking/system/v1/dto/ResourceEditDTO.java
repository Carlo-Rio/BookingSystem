package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Location;
import lombok.Data;

@Data
public class ResourceEditDTO {


    private String resourceName;

    private Location location;

    private Integer capacity;

}
