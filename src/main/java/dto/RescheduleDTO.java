package dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class RescheduleDTO {

    @NotNull
    private LocalDateTime newStartTime;

    @NotNull
    private LocalDateTime newEndTime;

}
