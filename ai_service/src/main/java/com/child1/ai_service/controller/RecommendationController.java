package com.child1.ai_service.controller;

import com.child1.ai_service.ResourceNotFoundException;
import com.child1.ai_service.model.Recommendation;
import com.child1.ai_service.service.RecommendationService;
import com.child1.commonsecurity.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/recommendations")

public class RecommendationController {

    private final RecommendationService recommendationService;
    private final JwtService jwtService;

    @Autowired
    public RecommendationController(JwtService jwtService, RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Recommendation>> getAllRecommendations(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Recommendation> recommendations = recommendationService.getAllRecommendations(pageable);
        return ResponseEntity.ok(recommendations);
    }


    @GetMapping
    public ResponseEntity<Page<Recommendation>> getMyRecommendations(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        String jwtToken = extractJwtFromHeader(authorizationHeader);
        System.out.println("Extracted JWT Token: " + jwtToken);
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Long userId = jwtService.extractUserId(jwtToken);
        System.out.println("Extracted User ID: " + userId);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Recommendation> recommendations = recommendationService.getRecommendationsByUserId(userId, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recommendation> getRecommendationById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable @NotBlank(message = "ID cannot be blank") String id) {
        String jwtToken = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(jwtToken);

        try {
            Recommendation recommendation = recommendationService.getRecommendationById(id);

            // Check if user owns this recommendation
            if (!recommendation.getUserId().equals(userId)) {
                return ResponseEntity.notFound().build(); // Return 404 instead of 403 for security
            }

            return ResponseEntity.ok(recommendation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<Recommendation>> getRecommendationsByActivityId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable @NotBlank(message = "Activity ID cannot be blank") String activityId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        String jwtToken = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(jwtToken);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Recommendation> recommendations = recommendationService.getRecommendationsByUserIdAndActivityId(
                userId, activityId, pageable);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping
    public ResponseEntity<Recommendation> createRecommendation(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody Recommendation recommendation) {
        String token = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(token);

        recommendation.setUserId(userId);

        try {
            Recommendation savedRecommendation = recommendationService.saveRecommendation(recommendation);
            URI location = URI.create("/api/v1/recommendations/" + savedRecommendation.getId());
            return ResponseEntity.created(location).body(savedRecommendation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recommendation> updateRecommendation(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable @NotBlank(message = "ID cannot be blank") String id,
            @Valid @RequestBody Recommendation recommendation) {

        String token = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(token);

        try {
            // Verify ownership
            Recommendation existingRecommendation = recommendationService.getRecommendationById(id);
            if (!existingRecommendation.getUserId().equals(userId)) {
                return ResponseEntity.notFound().build();
            }

            recommendation.setUserId(userId);

            Recommendation updatedRecommendation = recommendationService.updateRecommendation(id, recommendation);
            return ResponseEntity.ok(updatedRecommendation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable @NotBlank(message = "ID cannot be blank") String id) {

        String jwtToken = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(jwtToken);

        try {
            // Verify ownership
            Recommendation existingRecommendation = recommendationService.getRecommendationById(id);
            if (!existingRecommendation.getUserId().equals(userId)) {
                return ResponseEntity.notFound().build();
            }

            recommendationService.deleteRecommendation(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/my-recommendations")
    public ResponseEntity<Void> deleteMyRecommendations(
            @RequestHeader("Authorization") String authorizationHeader) {

        String jwtToken = extractJwtFromHeader(authorizationHeader);
        Long userId = jwtService.extractUserId(jwtToken);

        recommendationService.deleteRecommendationsByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    private String extractJwtFromHeader(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7).trim();
        }
        return tokenHeader != null ? tokenHeader.trim() : null;
    }
}
