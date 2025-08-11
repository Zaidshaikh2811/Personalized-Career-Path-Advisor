package com.child1.activity_service.Model;


import com.child1.activity_service.Dto.ActivityResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;


@Document(collection = "activities")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    private String id;

    private String userId;

    private ActivitType activityType;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;

    private Map<String, Object> additionalMetrics;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;


    public void updateFromDto(ActivityResponseDto response) {
        this.activityType = response.getActivityType();
        this.duration = response.getDuration();
        this.caloriesBurned = response.getCaloriesBurned();
        this.startTime = response.getStartTime();
        this.additionalMetrics = response.getAdditionalMetrics();
    }
}
