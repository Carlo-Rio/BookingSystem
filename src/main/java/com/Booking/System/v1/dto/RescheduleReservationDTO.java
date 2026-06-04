package com.booking.system.v1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleReservationDTO {


    @NotNull
    private LocalDateTime newStartTime;

    @NotNull
    private LocalDateTime newEndTime;

}
