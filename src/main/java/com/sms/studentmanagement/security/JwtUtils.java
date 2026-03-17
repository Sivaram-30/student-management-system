package com.sms.studentmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    // Generate JWT token
    public String generateJwtToken(Authentication authentication) {
        UserDetails user =
            (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(
                    System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // Create secret key
    private SecretKey key() {
        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(jwtSecret));
    }

    // Get username from token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Validate token
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            System.err.println("JWT error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT empty: " + e.getMessage());
        }
        return false;
    }
}