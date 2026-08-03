package com.eduguide.eduguide.config;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // 1. We generate a secure key specifically for the HS256 algorithm
    // We keep this as a field because we will need it later to verify tokens
    private final SecretKey jwtSecretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // "setSubject" is now just "subject"
                .issuedAt(new Date()) // "setIssuedAt" is now "issuedAt"
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(jwtSecretKey) // We sign with the key directly
                .compact();
    }



    // Add these to your existing JwtService.java class:

    public String extractUsername(String token) {
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
