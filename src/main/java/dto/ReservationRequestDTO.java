package dto;

import entity.Resource;
import entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {


    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    @NotNull(message = "user ID is required")
    private Long userId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;




}
