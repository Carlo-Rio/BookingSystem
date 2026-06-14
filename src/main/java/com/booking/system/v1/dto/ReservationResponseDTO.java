package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Location;
import com.booking.system.v1.entity.ReservationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationResponseDTO {

    private Long reservationId;

    private String username;

    private String resourceName;

    private Location location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private ReservationStatus status;




}
