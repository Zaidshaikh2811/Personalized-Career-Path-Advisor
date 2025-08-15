package com.child1.commonsecurity;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtService {

    private final String secretKey="asdasdasdasdasd";
    private final long expiration=100000;

//    public JwtService(
//            @Value("${jwt.secret}") String secretKey,
//            @Value("${jwt.expiration}") long expiration) {
//        System.out.println("Initializing); JwtService with secretKey: " + secretKey + " and expiration: " + expiration);
//        this.secretKey = secretKey;
//        this.expiration = expiration;
//    }

    public String generateToken(Long userId, String username) {
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
}
