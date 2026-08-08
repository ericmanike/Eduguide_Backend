package com.eduguide.eduguide.config;

import com.eduguide.eduguide.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        // Convert secret string to SecretKey (minimum 32 characters for HS256)
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtSecretKey)
                .compact();
    }



    // Add these to your existing JwtService.java class:

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey) // Verifies the signature using our secret key
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.after(new Date()); // Returns true if the token is not expired
        } catch (Exception e) {
            return false; // Returns false if the token is tampered with or broken
        }
    }

}
