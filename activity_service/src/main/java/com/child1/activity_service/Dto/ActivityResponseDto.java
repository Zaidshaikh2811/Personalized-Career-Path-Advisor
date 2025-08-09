package com.child1.activity_service.Dto;


import com.child1.activity_service.Model.ActivitType;
import com.child1.activity_service.Model.Activity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityResponseDto {

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
                .activityType(activityType)
                .duration(duration)
                .caloriesBurned(caloriesBurned)
                .startTime(startTime)
                .additionalMetrics(additionalMetrics)
                .build();
    }
}
