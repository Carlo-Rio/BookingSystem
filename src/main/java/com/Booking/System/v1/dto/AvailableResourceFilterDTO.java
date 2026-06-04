package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Location;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailableResourceFilterDTO {

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Location is required")
    private Location location;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}