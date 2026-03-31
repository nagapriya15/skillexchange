package com.example.skillexchange.security;

import com.example.skillexchange.model.User;
import com.example.skillexchange.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * CustomUserDetailsService - Tells Spring Security how to load user data.
 *
 * Spring Security needs a UserDetailsService to:
 * 1. Look up a user by their email (we use email instead of username)
 * 2. Return a UserDetails object that Spring Security understands
 * 3. This is used during login to verify the user's credentials
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by email.
     * Spring Security calls this method during authentication.
     *
     * @param email - the user's email (used as the "username" in our app)
     * @return UserDetails object containing user info for Spring Security
     * @throws UsernameNotFoundException if user with given email doesn't exist
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user in our database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Return a Spring Security UserDetails object
        // Parameters: username (email), password, authorities (roles/permissions)
        // We use an empty list for authorities since we haven't implemented roles yet
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()  // No roles/authorities for now
        );
    }
}
