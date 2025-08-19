package com.child1.activity_service.service;


import com.child1.activity_service.Dto.ActivityRequestDto;
import com.child1.activity_service.Dto.ActivityResponseDto;
import com.child1.activity_service.Model.Activity;
import com.child1.activity_service.repo.ActivityRepo;
import com.child1.commonsecurity.JwtService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.dao.DataAccessException;
import com.child1.activity_service.messaging.ActivityUpdateMessage;
import com.child1.activity_service.messaging.ActivityDeleteMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepo activityRepo;

    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;


    public ActivityService(ActivityRepo activityRepo, RabbitTemplate rabbitTemplate, JwtService jwtService) {
        this.activityRepo = activityRepo;
        this.rabbitTemplate = rabbitTemplate;

        this.jwtService = jwtService;

    }


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.update.routing.key}")
    private String updateRoutingKey;
    @Value("${rabbitmq.delete.routing.key}")
    private String deleteRoutingKey;



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
        if (activity.getUserId() != null && !activity.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User ID in request does not match authenticated user");
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

    public ActivityResponseDto updateActivity(String id, ActivityRequestDto activity , String authHeader) {
        Long userId = jwtService.extractUserId(authHeader);
        Optional<Activity> existingActivityOpt = activityRepo.findByIdAndUserId(id, userId);
        if (existingActivityOpt.isEmpty()) {
            throw new RuntimeException("Activity not found or access denied");
        }
        Activity existingActivity = existingActivityOpt.get();
        updateActivityFields(existingActivity, activity);
        try {
            activityRepo.save(existingActivity);
        } catch (DataAccessException e) {
            logger.error("Database error while updating activity", e);
            throw new RuntimeException("Database error while updating activity", e);
        }
        try {
            ActivityUpdateMessage updateMessage = new ActivityUpdateMessage();
            updateMessage.setActivityId(id);
            updateMessage.setActivity(existingActivity);
            updateMessage.setAction("UPDATE");
            rabbitTemplate.convertAndSend(exchange, updateRoutingKey, updateMessage);
            logger.info("Activity update sent to RabbitMQ: {}", id);
        } catch (Exception e) {
            logger.error("Failed to send activity update to RabbitMQ", e);
        }
        return mapToResponseDto(existingActivity);
    }

    private void updateActivityFields(Activity existingActivity, ActivityRequestDto activity) {
        if (activity.getActivityType() != null && !activity.getActivityType().toString().trim().isEmpty()) {
            existingActivity.setActivityType(activity.getActivityType());
        }
        if (activity.getDuration() != null && activity.getDuration() > 0) {
            existingActivity.setDuration(activity.getDuration());
        }
        if (activity.getCaloriesBurned() != null && activity.getCaloriesBurned() >= 0) {
            existingActivity.setCaloriesBurned(activity.getCaloriesBurned());
        }
        if (activity.getStartTime() != null && !activity.getStartTime().isAfter(LocalDateTime.now())) {
            existingActivity.setStartTime(activity.getStartTime());
        }
        if (activity.getAdditionalMetrics() != null) {
            existingActivity.setAdditionalMetrics(activity.getAdditionalMetrics());
        }
    }

    public void deleteActivity(String id , String authHeader) {
        Long userId = jwtService.extractUserId(authHeader);
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity id cannot be null or empty");
        }
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }
        try {
            activityRepo.deleteByUserIdAndId(userId, id);
            ActivityDeleteMessage deleteMessage = new ActivityDeleteMessage();
            deleteMessage.setActivityId(id);
            deleteMessage.setUserId(userId);
            deleteMessage.setAction("DELETE");
            rabbitTemplate.convertAndSend(exchange, deleteRoutingKey, deleteMessage);
            logger.info("Activity deletion message sent to RabbitMQ: {}", id);

        } catch (DataAccessException e) {
            logger.error("Database error while deleting activity", e);
            throw new RuntimeException("Database error while deleting activity", e);
        }
    }

    public ActivityResponseDto getActivityById(String id, String authHeader) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid activity id");
        }

        Long userId = jwtService.extractUserId(authHeader);

        try {
            Optional<Activity> activity = activityRepo.findByIdAndUserId(id, userId);
            if (activity.isEmpty()) {
                throw new RuntimeException("Activity not found or access denied");
            }
            return mapToResponseDto(activity.get());
        } catch (DataAccessException e) {
            logger.error("Database error while fetching activity by id", e);
            throw new RuntimeException("Database error while fetching activity by id", e);
        }
    }

    public List<ActivityResponseDto> getRecentActivities(String token, int limit) {
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        try {
            List<Activity> activities = limit <= 5 ?
                    activityRepo.findTop5ByUserIdOrderByStartTimeDesc(userId) :
                    activityRepo.findByUserIdOrderByStartTimeDesc(userId);

            return activities.stream()
                    .limit(limit)
                    .map(this::mapToResponseDto)
                    .toList();
        } catch (DataAccessException e) {
            logger.error("Database error while fetching recent activities", e);
            throw new RuntimeException("Database error while fetching recent activities", e);
        }
    }

    public Page<ActivityResponseDto> getActivitiesByUser(Long userId, int page, int size, String sortBy, String sortDirection) {
        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Activity> activityPage = activityRepo.findByUserId(userId, pageable);
            return activityPage.map(this::mapToResponseDto);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while fetching user activities", e);
        }
    }

    public Page<ActivityResponseDto> getAllActivitiesPaginated(int page, int size, String sortBy, String sortDirection) {
        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Activity> activityPage = activityRepo.findAll(pageable);
            return activityPage.map(this::mapToResponseDto);
        } catch (DataAccessException e) {
            logger.error("Database error while fetching paginated activities", e);
            throw new RuntimeException("Database error while fetching paginated activities", e);
        }
    }

    public List<ActivityResponseDto> getTopCalorieActivities(String authHeader) {
        Long userId = jwtService.extractUserId(authHeader);

        try {
            List<Activity> activities = activityRepo.findTop10ByUserIdOrderByCaloriesBurnedDesc(userId);
            return activities.stream().map(this::mapToResponseDto).toList();
        } catch (DataAccessException e) {
            logger.error("Database error while fetching top calorie activities", e);
            throw new RuntimeException("Database error while fetching top calorie activities", e);
        }
    }

    public ActivityService.ActivityStatsDto getActivityStats(String authHeader, LocalDateTime startDate, LocalDateTime endDate) {
        Long userId = jwtService.extractUserId(authHeader);

        try {
            long totalActivities = startDate != null && endDate != null ?
                    activityRepo.countByUserIdAndStartTimeBetween(userId, startDate, endDate) :
                    activityRepo.countByUserId(userId);

            List<Activity> activities = startDate != null && endDate != null ?
                    activityRepo.findByUserIdAndStartTimeBetween(userId, startDate, endDate, Pageable.unpaged()).getContent() :
                    activityRepo.findByUserId(userId, Pageable.unpaged()).getContent();

            int totalCalories = activities.stream()
                    .mapToInt(Activity::getCaloriesBurned)
                    .sum();

            int totalDuration = activities.stream()
                    .mapToInt(Activity::getDuration)
                    .sum();

            double avgCaloriesPerActivity = totalActivities > 0 ? (double) totalCalories / totalActivities : 0;
            double avgDurationPerActivity = totalActivities > 0 ? (double) totalDuration / totalActivities : 0;

            return new ActivityStatsDto(
                    totalActivities,
                    totalCalories,
                    totalDuration,
                    avgCaloriesPerActivity,
                    avgDurationPerActivity
            );
        } catch (DataAccessException e) {
            logger.error("Database error while calculating activity stats", e);
            throw new RuntimeException("Database error while calculating activity stats", e);
        }
    }

    public Page<ActivityResponseDto> getActivitiesForAuthenticatedUser(String token, int page, int size, String sortBy, String sortDirection) {
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        try {
            Page<Activity> activityPage = activityRepo.findByUserId(userId, pageable);
            return activityPage.map(this::mapToResponseDto);
        } catch (DataAccessException e) {
            logger.error("Database error while fetching activities for authenticated user", e);
            throw new RuntimeException("Database error while fetching activities for authenticated user", e);
        }
    }

    public Page<ActivityResponseDto> getActivitiesWithFiltersForUser(String token, String activityType, LocalDateTime startDate, LocalDateTime endDate, Integer minDuration, Integer maxDuration, Integer minCalories, Integer maxCalories, int page, int size, String sortBy, String sortDirection) {
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId missing");
        }



        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<Activity> activityPage;

            // Simplified filtering - start with basic queries that work
            if (userId != null) {
                activityPage = activityRepo.findByUserId(userId, pageable);
            } else {
                activityPage = activityRepo.findAll(pageable);
            }

            // Apply additional filters programmatically if needed
            if (activityType != null || startDate != null || endDate != null ||
                    minDuration != null || maxDuration != null || minCalories != null || maxCalories != null) {

                // For complex filtering, get all results and filter in memory
                // This is less efficient but works for smaller datasets
                List<Activity> filteredActivities = activityPage.getContent().stream()
                    .filter(activity -> activityType == null || activity.getActivityType().toString().equalsIgnoreCase(activityType) )
                    .filter(activity -> startDate == null || !activity.getStartTime().isBefore(startDate))
                    .filter(activity -> endDate == null || !activity.getStartTime().isAfter(endDate))
                    .filter(activity -> minDuration == null || activity.getDuration() >= minDuration)
                    .filter(activity -> maxDuration == null || activity.getDuration() <= maxDuration)
                    .filter(activity -> minCalories == null || activity.getCaloriesBurned() >= minCalories)
                    .filter(activity -> maxCalories == null || activity.getCaloriesBurned() <= maxCalories)
                    .toList();

                // Create a new Page with filtered results (simplified approach)
                return new PageImpl<>(
                        filteredActivities.stream().map(this::mapToResponseDto).toList(),
                        pageable,
                        filteredActivities.size()
                );
            }


            return activityPage.map(this::mapToResponseDto);
        } catch (DataAccessException e) {
            logger.error("Database error while fetching filtered activities for user", e);
            throw new RuntimeException("Database error while fetching filtered activities for user", e);
        }

    }





    public static class ActivityStatsDto {
        private final long totalActivities;
        private final int totalCalories;
        private final int totalDuration;
        private final double avgCaloriesPerActivity;
        private final double avgDurationPerActivity;

        public ActivityStatsDto(long totalActivities, int totalCalories, int totalDuration,
                                double avgCaloriesPerActivity, double avgDurationPerActivity) {
            this.totalActivities = totalActivities;
            this.totalCalories = totalCalories;
            this.totalDuration = totalDuration;
            this.avgCaloriesPerActivity = avgCaloriesPerActivity;
            this.avgDurationPerActivity = avgDurationPerActivity;
        }
    }
}
