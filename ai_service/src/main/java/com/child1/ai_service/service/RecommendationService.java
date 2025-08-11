package com.child1.ai_service.service;

import com.child1.ai_service.ResourceNotFoundException;
import com.child1.ai_service.model.Recommendation;
import com.child1.ai_service.repo.RecommendationRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class RecommendationService {

    private final RecommendationRepo recommendationRepo;



    public List<Recommendation> getAllRecommendations() {
        return recommendationRepo.findAll();
    }

    public Recommendation getRecommendationById(String id) {
        return recommendationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation with id " + id + " not found"));
    }

    public Recommendation saveRecommendation(Recommendation recommendation) {
        if (recommendation == null) {
            throw new IllegalArgumentException("Recommendation cannot be null");
        }
        return recommendationRepo.save(recommendation);
    }

    public Recommendation updateRecommendation(String id, Recommendation recommendation) {
        if (!recommendationRepo.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }
        recommendation.setId(id);
        return recommendationRepo.save(recommendation);
    }

    public void deleteRecommendation(String id) {
        if (!recommendationRepo.existsById(id)) {
            throw new ResourceNotFoundException("Recommendation with id " + id + " not found");
        }
        recommendationRepo.deleteById(id);
    }
}
