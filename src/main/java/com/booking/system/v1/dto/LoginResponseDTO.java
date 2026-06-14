package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Role;
import lombok.Data;

@Data
public class LoginResponseDTO {

    private String token;

    private String email;

    private String username;

    private Role role;

}
