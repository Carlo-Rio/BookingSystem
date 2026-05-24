package dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "First name ist not null")
    private String firstName;

    @NotBlank(message = "Last name ist not null")
    private String lastName;

    @NotBlank(message = "Username ist not null")
    private String username;



}
