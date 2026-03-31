package com.example.skillexchange.config;

import com.example.skillexchange.security.JwtFilter;
import com.example.skillexchange.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Configures Spring Security for JWT authentication.
 *
 * Key configurations:
 * 1. Disable CSRF (not needed for REST APIs with JWT)
 * 2. Allow public access to /api/auth/** (register & login)
 * 3. Require authentication for all other endpoints
 * 4. Use STATELESS session (no server-side sessions, JWT handles state)
 * 5. Add JwtFilter BEFORE Spring's default authentication filter
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    // ==================== SECURITY FILTER CHAIN ====================

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 0. Enable CORS for frontend communication
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 1. Disable CSRF - not needed because we use JWT tokens (not cookies)
                .csrf(csrf -> csrf.disable())

                // 2. Configure which endpoints are public vs protected
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // Public: register & login
                        .requestMatchers("/api/hello").permitAll()     // Public: test endpoint
                        .requestMatchers("/api/health").permitAll()    // Public: health check
                        .anyRequest().authenticated()                  // Everything else: requires JWT
                )

                // 3. Make session STATELESS (no server-side sessions)
                // Each request must carry its own JWT token for authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Add our JwtFilter BEFORE Spring's default authentication filter
                // This ensures our filter processes the JWT token first
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ==================== BEANS ====================

    /**
     * BCryptPasswordEncoder - Encrypts passwords before storing in database.
     * BCrypt is a strong one-way hashing algorithm.
     * Example: "password123" → "$2a$10$N9qo8uLOick..."
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager - Manages the authentication process.
     * Used in AuthController to authenticate login requests.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
