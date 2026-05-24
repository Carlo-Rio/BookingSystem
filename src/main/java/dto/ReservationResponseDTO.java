package dto;

import entity.ReservationStatus;
import entity.ResourceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationResponseDTO {

    private Long reservationId;

    private String username;

    private String resourceName;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private ReservationStatus status;




}
