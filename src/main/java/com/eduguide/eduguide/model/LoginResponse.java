package com.eduguide.eduguide.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
}
