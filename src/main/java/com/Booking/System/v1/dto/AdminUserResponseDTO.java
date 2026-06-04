package com.booking.system.v1.dto;

import com.booking.system.v1.entity.Role;
import com.booking.system.v1.entity.UserStatus;

public record AdminUserResponseDTO(String username,
                                   String firstName,
                                   String lastName,
                                   String email,
                                   UserStatus status,
                                   Role role) {

}
