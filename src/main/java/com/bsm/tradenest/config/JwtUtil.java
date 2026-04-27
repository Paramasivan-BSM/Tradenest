package com.bsm.tradenest.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 256+ bit secret (OK)
    private static final String SECRET =
            "r9ZPp8+Hk0Vf5Ue3PZrR6cG9gqYx0yD8X2F9QmKJvA4=";

    // Access Token validity: 1 hour
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 hour

    // Refresh Token validity: 7 days (Example)
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7;

    // Create key ONCE
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Generate Access Token
    public String generateToken(String email, String role) {
        return createToken(email, role, ACCESS_TOKEN_VALIDITY);
    }

    // Generate Refresh Token
    public String generateRefreshToken(String email) {
        // Refresh tokens typically don't need the role, just the subject (email)
        // You can add it if you want, but usually you fetch fresh roles from DB on refresh
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createToken(String email, String role, long validity) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
