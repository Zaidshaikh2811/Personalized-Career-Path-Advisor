package com.child1.auth_service.controller;

import com.child1.auth_service.Dto.LoginRequest;
import com.child1.auth_service.Dto.TokenResponse;
import com.child1.auth_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth Service is running");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Received login request for user: " + request.getUsername());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {
        System.out.println("Received registration request for user: " + request.getUsername());
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/validate-token/{token}")
    public ResponseEntity<Map<String, Boolean>> validateToken(@PathVariable String token) {
        System.out.println("Received token for validation: " + token);
        boolean isValid = authService.validateToken(token);
        System.out.println("Token validation result: " + isValid);
        if (!isValid) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }
        System.out.println("Token is valid");
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}
