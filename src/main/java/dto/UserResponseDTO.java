package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import entity.Role;
import entity.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserStatus status;
    private Role role;

}

