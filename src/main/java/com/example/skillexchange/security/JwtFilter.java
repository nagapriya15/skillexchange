package com.example.skillexchange.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtFilter - Intercepts EVERY incoming HTTP request.
 *
 * HOW THIS FILTER WORKS:
 * 1. Client sends request with header: "Authorization: Bearer <jwt_token>"
 * 2. This filter extracts the token from the header
 * 3. Validates the token using JwtUtil
 * 4. If valid, loads user details and sets authentication in SecurityContext
 * 5. Spring Security then allows the request to proceed
 *
 * This filter runs ONCE per request (extends OncePerRequestFilter).
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String token = null;

        // Step 2: Check if the header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token (remove "Bearer " prefix)
            token = authorizationHeader.substring(7);

            try {
                // Extract email from the token
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                // Token is invalid or expired - just continue without authentication
                System.out.println("JWT Token validation failed: " + e.getMessage());
            }
        }

        // Step 3: If we got an email and no authentication exists yet in the context
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Step 4: Validate the token against the user details
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                // Step 5: Create an authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,    // Principal (user info)
                                null,           // Credentials (not needed after auth)
                                userDetails.getAuthorities()  // Roles/authorities
                        );

                // Add request details to the authentication token
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 6: Set the authentication in Spring Security's context
                // This tells Spring Security "this user is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Step 7: Continue the filter chain (pass request to next filter/controller)
        filterChain.doFilter(request, response);
    }
}
