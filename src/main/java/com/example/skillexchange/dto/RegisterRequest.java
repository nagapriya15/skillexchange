package com.example.skillexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest DTO - Carries registration data from client to server.
 * Client sends: { "username": "...", "email": "...", "password": "..." }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
}
