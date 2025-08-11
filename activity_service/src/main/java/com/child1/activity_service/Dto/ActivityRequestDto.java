package com.child1.activity_service.Dto;


import com.child1.activity_service.Model.ActivitType;
import com.child1.activity_service.Model.Activity;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequestDto {



    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Activity type is required")
    private ActivitType activityType;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotNull(message = "Calories burned is required")
    @Min(value = 0, message = "Calories burned must be non-negative")
    private Integer caloriesBurned;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private Map<String,Object> additionalMetrics;

    public Activity toEntity() {
        Activity activity = new Activity();
        activity.setUserId(this.userId);
        activity.setActivityType(this.activityType);
        activity.setDuration(this.duration);
        activity.setCaloriesBurned(this.caloriesBurned);
        activity.setStartTime(this.startTime);
        activity.setAdditionalMetrics(this.additionalMetrics);
        return activity;
    }
}
