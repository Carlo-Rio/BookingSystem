package dto;

import entity.Role;
import entity.UserStatus;

public record AdminUserResponseDTO(String username,
                                   String firstName,
                                   String lastName,
                                   String email,
                                   UserStatus status,
                                   Role role) {

}
