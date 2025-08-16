package com.child1.activity_service.Dto;


import com.child1.activity_service.Model.ActivitType;
import com.child1.activity_service.Model.Activity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponseDto {

    @NotNull(message = "ID is required")
    @Min(value = 1, message = "ID must be a positive integer")
    private String id;

    @NotNull(message = "User ID is required")
    @Min(value = 1, message = "User ID must be a positive integer")
    private Long userId;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotNull(message = "Calories burned is required")
    @Min(value = 0, message = "Calories burned must be non-negative")
    private Integer caloriesBurned;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Activity type is required")
    private ActivitType activityType;

    private Map<String, Object> additionalMetrics;


    public Activity toEntity() {
        return Activity.builder()
                .userId(userId)
                .activityType(activityType)
                .duration(duration)
                .caloriesBurned(caloriesBurned)
                .startTime(startTime)
                .additionalMetrics(additionalMetrics)
                .build();
    }
}
