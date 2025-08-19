package com.child1.ai_service.repo;


import com.child1.ai_service.model.Recommendation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepo  extends MongoRepository<Recommendation, String> {

    Page<Recommendation> findByUserId(Long userId, Pageable pageable);

    Page<Recommendation> findByActivityId(String activityId, Pageable pageable);

    Page<Recommendation> findByUserIdAndActivityId(Long userId, String activityId, Pageable pageable);

    List<Recommendation> findByUserId(Long userId);

    Optional<Recommendation> findByActivityIdAndUserId(String activityId, @NotNull(message = "User ID is required") Long userId);
}
