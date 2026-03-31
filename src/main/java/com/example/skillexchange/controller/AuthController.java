package com.example.skillexchange.controller;

import com.example.skillexchange.dto.AuthResponse;
import com.example.skillexchange.dto.LoginRequest;
import com.example.skillexchange.dto.RegisterRequest;
import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.UserRepository;
import com.example.skillexchange.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - Handles user registration and login.
 *
 * Endpoints:
 * POST /api/auth/register → Register a new user
 * POST /api/auth/login    → Login and get JWT token
 *
 * These endpoints are PUBLIC (no token needed).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== REGISTER ====================

    /**
     * POST /api/auth/register
     *
     * Flow:
     * 1. Check if email already exists
     * 2. Encrypt the password using BCrypt
     * 3. Save the new user to the database
     * 4. Return success message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Check if email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Email is already registered!")
            );
        }

        // Create a new User entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // Encrypt password

        // Save to database
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully!")
        );
    }

    // ==================== LOGIN ====================

    /**
     * POST /api/auth/login
     *
     * Flow:
     * 1. Authenticate the user using email + password
     * 2. If valid, generate a JWT token
     * 3. Return the token to the client
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            // Authenticate using Spring Security's AuthenticationManager
            // This internally calls CustomUserDetailsService.loadUserByUsername()
            // and compares the encrypted password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // If authentication is successful, generate a JWT token
            String token = jwtUtil.generateToken(request.getEmail());

            // Return the token
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid email or password!")
            );
        }
    }
}
