package com.child1.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    @GetMapping
    public ResponseEntity<String> healthCheck() {
        System.out.println("Health check request received");
        return ResponseEntity.ok("Auth Service is running");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String request) {
        System.out.println("Login request received: " + request);

//        String token = jwtService.generateToken(request.getUsername());
//        return ResponseEntity.ok(new TokenResponse(token));
        // Placeholder for login logic
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @GetMapping("/validate-token/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        System.out.println("Token validation request received: " + token);
//        boolean isValid = jwtService.validateToken(token);
//        return ResponseEntity.ok(Map.of("valid", isValid));
        // Placeholder for token validation logic
        boolean isValid = true; // Assume token is valid for this example
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}
