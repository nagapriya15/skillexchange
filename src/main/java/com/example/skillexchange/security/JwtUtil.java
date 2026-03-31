package com.example.skillexchange.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * JwtUtil - Utility class for JWT operations.
 *
 * HOW JWT WORKS:
 * 1. User logs in with email + password
 * 2. Server validates credentials
 * 3. Server creates a JWT token containing the user's email and an expiration time
 * 4. Server sends the token back to the client
 * 5. Client sends this token in the "Authorization: Bearer <token>" header for every request
 * 6. Server validates the token on each request using this utility class
 */
@Component
public class JwtUtil {

    // Secret key used to sign and verify JWT tokens
    // In production, store this in application.properties or environment variables
    private static final String SECRET = "MySecretKeyForJWTTokenGenerationAndValidation2024SkillExchange";

    // Token validity: 24 hours (in milliseconds)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    // Create a signing key from the secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ==================== TOKEN GENERATION ====================

    /**
     * Generate a JWT token for the given email.
     * The token contains:
     * - Subject (user's email)
     * - Issued time
     * - Expiration time
     * - Digital signature
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                          // Who this token belongs to
                .issuedAt(new Date())                                    // When the token was created
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // When it expires
                .signWith(getSigningKey())                               // Sign with our secret key
                .compact();                                              // Build the token string
    }

    // ==================== TOKEN VALIDATION ====================

    /**
     * Validate the token:
     * 1. Extract the email from the token
     * 2. Check if it matches the expected email
     * 3. Check if the token has expired
     */
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }

    // ==================== EXTRACT INFORMATION ====================

    /**
     * Extract the email (subject) from the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the expiration date from the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse the token and extract all claims (payload data).
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // Use our secret key to verify
                .build()
                .parseSignedClaims(token)      // Parse the token
                .getPayload();                 // Get the payload (claims)
    }

    /**
     * Check if the token has expired.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
