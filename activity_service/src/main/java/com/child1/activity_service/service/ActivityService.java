package com.child1.activity_service.service;


import com.child1.activity_service.Dto.ActivityRequestDto;
import com.child1.activity_service.Dto.ActivityResponseDto;
import com.child1.activity_service.Model.Activity;
import com.child1.activity_service.repo.ActivityRepo;
import com.child1.commonsecurity.JwtService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Service
public class ActivityService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepo activityRepo;
    private final GetUser getUser;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;



    public ActivityService(ActivityRepo activityRepo, GetUser getUser, RabbitTemplate rabbitTemplate, JwtService jwtService) {
        this.activityRepo = activityRepo;
        this.rabbitTemplate = rabbitTemplate;
        this.getUser = getUser;
        this.jwtService = jwtService;
    }


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;


    public List<ActivityResponseDto> getAllActivities() {
        try {
            List<Activity> activityList = activityRepo.findAll();
            if (activityList.isEmpty()) {
                throw new IllegalStateException("No activities found");
            }
            return activityList.stream()
                    .map(activity -> {
                        ActivityResponseDto response = new ActivityResponseDto();
                        response.setId(activity.getId());
                        response.setUserId(activity.getUserId());
                        response.setActivityType(activity.getActivityType());
                        response.setDuration(activity.getDuration());
                        response.setCaloriesBurned(activity.getCaloriesBurned());
                        response.setStartTime(activity.getStartTime());
                        response.setAdditionalMetrics(activity.getAdditionalMetrics());
                        return response;
                    }).toList();
        } catch (DataAccessException e) {
            logger.error("Database error while fetching activities", e);
            throw new RuntimeException("Database error while fetching activities", e);
        }
    }

    public ActivityResponseDto createActivity(ActivityRequestDto activity, String authHeader) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity request cannot be null");
        }
        if (!StringUtils.hasText(authHeader)) {
            throw new IllegalArgumentException("Authorization header is required");
        }
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        Long userId;
        try {
            userId = jwtService.extractUserId(token);
        } catch (Exception e) {
            logger.error("Error extracting userId from token", e);
            throw new RuntimeException("Invalid token");
        }
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }
        if (!validateUserEmail(userId.toString())) {
            throw new RuntimeException("Invalid user email");
        }
        // Validate required fields in activity
        if (activity.getActivityType() == null) {
            throw new IllegalArgumentException("Activity type is required");
        }
        if (activity.getDuration() == null || activity.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (activity.getCaloriesBurned() == null || activity.getCaloriesBurned() < 0) {
            throw new IllegalArgumentException("Calories burned must be non-negative");
        }
        if (activity.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        Activity entity = activity.toEntity();
        entity.setUserId(userId);
        Activity savedEntity;
        try {
            savedEntity = activityRepo.save(entity);
        } catch (DataAccessException e) {
            logger.error("Database error while saving activity", e);
            throw new RuntimeException("Database error while saving activity", e);
        }
        ActivityResponseDto response = mapToResponseDto(savedEntity);
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedEntity);
            logger.info("Activity sent to RabbitMQ: {}", savedEntity);
        } catch (Exception e) {
            logger.error("Failed to send activity to RabbitMQ", e);
            throw new RuntimeException("Failed to send activity to RabbitMQ", e);
        }
        return response;
    }

    private ActivityResponseDto mapToResponseDto(Activity activity) {
        ActivityResponseDto response = new ActivityResponseDto();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        return response;
    }

    public ActivityResponseDto updateActivity(Long id, ActivityRequestDto activity) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid activity id");
        }
        if (activity == null) {
            throw new IllegalArgumentException("Activity request cannot be null");
        }
        Activity existingActivity = activityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        // Only update valid fields
        if (activity.getActivityType() != null) {
            existingActivity.setActivityType(activity.getActivityType());
        }
        if (activity.getDuration() != null && activity.getDuration() > 0) {
            existingActivity.setDuration(activity.getDuration());
        }
        if (activity.getCaloriesBurned() != null && activity.getCaloriesBurned() >= 0) {
            existingActivity.setCaloriesBurned(activity.getCaloriesBurned());
        }
        if (activity.getStartTime() != null) {
            existingActivity.setStartTime(activity.getStartTime());
        }
        if (activity.getAdditionalMetrics() != null) {
            existingActivity.setAdditionalMetrics(activity.getAdditionalMetrics());
        }
        try {
            activityRepo.save(existingActivity);
        } catch (DataAccessException e) {
            logger.error("Database error while updating activity", e);
            throw new RuntimeException("Database error while updating activity", e);
        }
        return mapToResponseDto(existingActivity);
    }

    public void deleteActivity(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid activity id");
        }
        if (!activityRepo.existsById(id)) {
            throw new RuntimeException("Activity not found");
        }
        try {
            activityRepo.deleteById(id);
        } catch (DataAccessException e) {
            logger.error("Database error while deleting activity", e);
            throw new RuntimeException("Database error while deleting activity", e);
        }
    }

    public boolean validateUserEmail(String email) {
        try {
            getUser.getUserByEmail(email);
            return true;
        } catch (Exception e) {
            logger.warn("User email validation failed: {}", email);
            return false;
        }
    }
}
