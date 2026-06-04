package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Location;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequestDTO {

    @NotNull(message = "resource Name is required")
    private String resourceName;

    @NotNull(message = "location is required")
    private Location location;

    @NotNull(message = "capacity is required")
    private Integer capacity;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer roomNumber;



}
