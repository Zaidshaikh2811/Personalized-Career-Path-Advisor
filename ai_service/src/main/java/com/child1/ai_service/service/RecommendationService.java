package com.child1.ai_service.service;

import com.child1.ai_service.ResourceNotFoundException;
import com.child1.ai_service.model.Recommendation;
import com.child1.ai_service.repo.RecommendationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Validated
@AllArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {

    private final RecommendationRepo recommendationRepo;

    public Page<Recommendation> getAllRecommendations(Pageable pageable) {

        return recommendationRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Recommendation getRecommendationById(String id) {
        validateStringId(id);

        return recommendationRepo.findById(id)
                .orElseThrow(() -> {

                    return new ResourceNotFoundException("Recommendation with id " + id + " not found");
                });
    }

    @Transactional(readOnly = true)
    public Page<Recommendation> getRecommendationsByUserId(Long userId, Pageable pageable) {
        validateLongId(userId);
        System.out.println("Fetching recommendations for user ID: " + userId);

        System.out.println("Pageable details - Page: " + pageable.getPageNumber() + ", Size: " + pageable.getPageSize() + ", Sort: " + pageable.getSort());

        return recommendationRepo.findByUserId(userId, pageable);
    }


    @Transactional(readOnly = true)
    public Page<Recommendation> getRecommendationsByUserIdAndActivityId(Long userId, String activityId, Pageable pageable) {
        validateLongId(userId);
        validateStringId(activityId);
        return recommendationRepo.findByUserIdAndActivityId(userId, activityId, pageable);
    }

    public Recommendation saveRecommendation(Recommendation recommendation) {
        // Set creation timestamp if not already set
        if (recommendation.getCreatedAt() == null) {
            recommendation.setCreatedAt(LocalDateTime.now());
        }
        recommendation.setUpdatedAt(LocalDateTime.now());

        Recommendation savedRecommendation = recommendationRepo.save(recommendation);

        return savedRecommendation;
    }

    public Recommendation updateRecommendation(String id, Recommendation recommendation) {
        validateStringId(id);


        // Check if recommendation exists
        if (!recommendationRepo.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }

        // Preserve the original ID and creation timestamp
        Recommendation existingRecommendation = getRecommendationById(id);
        recommendation.setId(id);
        recommendation.setCreatedAt(existingRecommendation.getCreatedAt());
        recommendation.setUpdatedAt(LocalDateTime.now());

        Recommendation updatedRecommendation = recommendationRepo.save(recommendation);

        return updatedRecommendation;
    }

    public void deleteRecommendation(String id) {
        validateStringId(id);

        if (!recommendationRepo.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }

        recommendationRepo.deleteById(id);
    }

    public void deleteRecommendationsByUserId(Long userId) {
        validateLongId(userId);

        List<Recommendation> userRecommendations = recommendationRepo.findByUserId(userId);
        if (!userRecommendations.isEmpty()) {
            recommendationRepo.deleteAll(userRecommendations);
        } else {
        }
    }

    // Private validation methods
    private void validateLongId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
    }

    private void validateStringId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("ID must not be null or empty");
        }
    }

}