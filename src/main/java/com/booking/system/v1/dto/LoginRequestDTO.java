package com.booking.system.v1.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}