package com.example.skillexchange.service;

import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * UserService - Handles all user-related business logic.
 *
 * Methods:
 * - registerUser()   → Create a new user with encrypted password
 * - getUserByEmail()  → Find user by email
 * - getUserById()     → Find user by ID
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user.
     * - Checks if email is already taken
     * - Encrypts password before saving
     *
     * @param username - the user's display name
     * @param email    - the user's unique email
     * @param password - the raw password (will be encrypted)
     * @return the saved User object
     * @throws RuntimeException if email already exists
     */
    public User registerUser(String username, String email, String password) {
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already registered: " + email);
        }

        // Create new user and encrypt the password
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Save to database and return the saved user
        return userRepository.save(user);
    }

    /**
     * Find a user by their email address.
     *
     * @param email - the email to search for
     * @return the User object
     * @throws RuntimeException if user not found
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Find a user by their ID.
     *
     * @param id - the user's ID
     * @return the User object
     * @throws RuntimeException if user not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
}
