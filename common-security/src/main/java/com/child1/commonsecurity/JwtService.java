package com.child1.commonsecurity;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtService {

    private final String secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expiration
    ) {
        System.out.println("Initializing");
        this.secretKey = secretKey;
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username) {
        System.out.println("Generating token for user: " + username);
        if (userId == null || username == null || username.isEmpty()) {
            throw new IllegalArgumentException("User ID and username must not be null or empty");
        }
        System.out.println("secretKey"+secretKey);
        System.out.println("expiration"+expiration);
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + token);
            if (token == null || token.isEmpty()) {
                System.out.println("Token is null or empty");
                return false;
            }
            System.out.println("Using secretKey: " + secretKey);
            System.out.println("Using expiration: " + expiration);
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }


    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    @PostConstruct
    public void checkEnv() {
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
    }
}
