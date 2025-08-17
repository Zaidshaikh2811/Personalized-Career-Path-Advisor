package com.child1.ai_service.repo;


import com.child1.ai_service.model.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepo  extends MongoRepository<Recommendation, String> {

    Page<Recommendation> findByUserId(Long userId, Pageable pageable);

    Page<Recommendation> findByActivityId(String activityId, Pageable pageable);

    Page<Recommendation> findByUserIdAndActivityId(Long userId, String activityId, Pageable pageable);

    // Non-paginated queries (for backward compatibility and specific use cases)
    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByActivityId(String activityId);

    List<Recommendation> findByUserIdAndActivityId(Long userId, String activityId);

    // Count queries - Fixed to use Long for userId consistently
    long countByUserId(Long userId);

    long countByActivityId(String activityId);

    long countByUserIdAndActivityId(Long userId, String activityId);

    // Latest recommendation queries - Fixed to use Long for userId consistently
    Optional<Recommendation> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Recommendation> findTopByActivityIdOrderByCreatedAtDesc(String activityId);

    List<Recommendation> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    // Date range queries
    Page<Recommendation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Recommendation> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDate,
                                                         LocalDateTime endDate, Pageable pageable);



    // Exists queries
    boolean existsByUserIdAndActivityId(Long userId, String activityId);

    // Custom queries using @Query annotation - Fixed to use Long for userId
    @Query("{ 'userId': ?0, 'rating': { $gte: ?1 } }")
    Page<Recommendation> findByUserIdWithMinRating(Long userId, Double minRating, Pageable pageable);

    @Query("{ 'activityId': ?0, 'createdAt': { $gte: ?1 } }")
    Page<Recommendation> findByActivityIdCreatedAfter(String activityId, LocalDateTime date, Pageable pageable);

    @Query(value = "{ 'userId': ?0 }", count = true)
    long countUserRecommendations(Long userId);


    void deleteByUserId(Long userId);

    void deleteByActivityId(String activityId);

    void deleteByUserIdAndActivityId(Long userId, String activityId);

    // Advanced search with multiple criteria
    @Query("{ $and: [ " +
            "{ 'userId': ?0 }, " +
            "{ $or: [ " +
            "  { 'title': { $regex: ?1, $options: 'i' } }, " +
            "  { 'description': { $regex: ?1, $options: 'i' } } " +
            "] } " +
            "] }")
    Page<Recommendation> findByUserIdAndSearchTerm(Long userId, String searchTerm, Pageable pageable);

}
