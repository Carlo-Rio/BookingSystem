package dto;

import entity.Location;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResourceRequestDTO {

    @NotNull(message = "resource Name is required")
    private String resourceName;

    @NotNull(message = "location is required")
    private Location location;

    @NotNull(message = "capacity is required")
    private Integer capacity;

}
