package com.child1.activity_service.repo;


import com.child1.activity_service.Model.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepo extends MongoRepository<Activity, Long> {


    List<Activity> findTop5ByUserIdOrderByStartTimeDesc(Long userId);

    List<Activity> findByUserIdOrderByStartTimeDesc(Long userId);
    @Query("{ 'userId': ?0, 'activityType': ?1, 'startTime': { $gte: ?2, $lte: ?3 } }")
    Page<Activity> findByUserIdAndActivityTypeAndDateRange(Long userId, String activityType, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Activity> findByUserIdAndStartTimeBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Activity> findByUserIdAndActivityType(Long userId, String activityType, Pageable pageable);

    Page<Activity> findByUserId(Long userId, Pageable pageable);

    Page<Activity> findByActivityType(String activityType, Pageable pageable);

    Page<Activity> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Activity> findByDurationBetween(Integer minDuration, Integer maxDuration, Pageable pageable);

    Page<Activity> findByCaloriesBurnedBetween(Integer minCalories, Integer maxCalories, Pageable pageable);

    List<Activity> findTop10ByUserIdOrderByCaloriesBurnedDesc(Long userId);

    long countByUserIdAndStartTimeBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    long countByUserId(Long userId);


    Optional<Activity> findByIdAndUserId(Long id, Long userId);

 
    boolean existsByUserIdAndId(Long userId, Long id);

    void deleteByUserIdAndId(Long userId, Long id);
}
