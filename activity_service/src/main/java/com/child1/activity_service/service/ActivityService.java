package com.child1.activity_service.service;


import com.child1.activity_service.Dto.ActivityRequestDto;
import com.child1.activity_service.Dto.ActivityResponseDto;
import com.child1.activity_service.Model.Activity;
import com.child1.activity_service.repo.ActivityRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private ActivityRepo activityRepo;
    private final GetUser getUser;
    private RabbitTemplate rabbitTemplate;
    public ActivityService(ActivityRepo activityRepo, GetUser getUser, RabbitTemplate rabbitTemplate) {
        this.activityRepo = activityRepo;
        this.rabbitTemplate = rabbitTemplate;
        this.getUser = getUser;
    }


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.queue.name}")
    private String queueName;


    public List<ActivityResponseDto> getAllActivities() {
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



    }

    public ActivityResponseDto createActivity(ActivityRequestDto activity) {
        // Convert DTO to entity and save to DB
        Activity entity = activity.toEntity();
        Activity savedEntity = activityRepo.save(entity);

        // Convert saved entity to response DTO (with generated id)
        ActivityResponseDto response = new ActivityResponseDto();
        response.setId(savedEntity.getId());
        response.setUserId(savedEntity.getUserId());
        response.setActivityType(savedEntity.getActivityType());
        response.setDuration(savedEntity.getDuration());
        response.setCaloriesBurned(savedEntity.getCaloriesBurned());
        response.setStartTime(savedEntity.getStartTime());
        response.setAdditionalMetrics(savedEntity.getAdditionalMetrics());

        System.out.println("Activity created: " + response);

        try {
            // Send the full saved entity (with id) to RabbitMQ
            rabbitTemplate.convertAndSend(exchange, routingKey, savedEntity);
            System.out.println("Activity sent to RabbitMQ: " + savedEntity);
        } catch (Exception e) {
            System.err.println("Failed to send activity to RabbitMQ: " + e.getMessage());
            throw new RuntimeException("Failed to send activity to RabbitMQ", e);
        }
        return response;
    }

    public ActivityResponseDto updateActivity(Long id, ActivityRequestDto activity) {



        ActivityResponseDto response = new ActivityResponseDto();
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());

        Activity existingActivity = activityRepo.findById(id).orElseThrow(() -> new RuntimeException("Activity not found"));
        existingActivity.updateFromDto(response);
        activityRepo.save(existingActivity);

        return response;
    }

    public void deleteActivity(Long id) {
        if (!activityRepo.existsById(id)) {
            throw new RuntimeException("Activity not found");
        }
        activityRepo.deleteById(id);
    }


    public boolean validateUserEmail(String email) {
        try {
            getUser.getUserByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
