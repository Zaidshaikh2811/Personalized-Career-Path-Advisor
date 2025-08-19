package com.child1.activity_service.controller;


import com.child1.activity_service.Dto.ActivityRequestDto;
import com.child1.activity_service.Dto.ActivityResponseDto;
import com.child1.activity_service.service.ActivityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService activityService;


    @GetMapping
    public ResponseEntity<Page<ActivityResponseDto>> getActivitiesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        System.out.println("Fetching paginated activities - Page: " + page + ", Size: " + size);
        return ResponseEntity.ok(activityService.getAllActivitiesPaginated(page, size, sortBy, sortDirection));
    }



    @GetMapping("/my-activities")
    public ResponseEntity<Page<ActivityResponseDto>> getMyActivities(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching activities for authenticated user");
        return ResponseEntity.ok(activityService.getActivitiesForAuthenticatedUser(jwt, page, size, sortBy, sortDirection));
    }

    @GetMapping("/my-activities/filtered")
    public ResponseEntity<Page<ActivityResponseDto>> getMyActivitiesWithFilters(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) Integer minCalories,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching filtered activities for authenticated user");
        return ResponseEntity.ok(activityService.getActivitiesWithFiltersForUser(
                jwt, activityType, startDate, endDate, minDuration, maxDuration,
                minCalories, maxCalories, page, size, sortBy, sortDirection));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivityById(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching activity by id: " + id);
        return ResponseEntity.ok(activityService.getActivityById(id, jwt));
    }

    // Get recent activities for authenticated user
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityResponseDto>> getRecentActivities(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "5") int limit) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching recent activities, limit: " + limit);
        return ResponseEntity.ok(activityService.getRecentActivities(jwt, limit));
    }

    @GetMapping("/top-calories")
    public ResponseEntity<List<ActivityResponseDto>> getTopCalorieActivities(
            @RequestHeader("Authorization") String token) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching top calorie activities");
        return ResponseEntity.ok(activityService.getTopCalorieActivities(jwt));
    }

    // Get activity statistics for authenticated user
    @GetMapping("/stats")
    public ResponseEntity<ActivityService.ActivityStatsDto> getActivityStats(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String jwt = extractJwtFromHeader(token);
        System.out.println("Fetching activity statistics");
        return ResponseEntity.ok(activityService.getActivityStats(jwt, startDate, endDate));
    }


    @PostMapping("/create")
    public ResponseEntity<ActivityResponseDto> createActivity(@Valid @RequestBody ActivityRequestDto activity ,
                                                              @RequestHeader("Authorization") String token) {
        String jwt = extractJwtFromHeader(token);
        System.out.println("Creating activity: " + activity + " with token: " + jwt);
        return   ResponseEntity.ok(activityService.createActivity(activity, jwt));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ActivityResponseDto> updateActivity(@PathVariable String id, @Valid @RequestBody ActivityRequestDto activity, @RequestHeader("Authorization") String token) {
        String jwt = extractJwtFromHeader(token);
        return ResponseEntity.ok(activityService.updateActivity(id, activity, jwt));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id, @RequestHeader("Authorization") String token) {
        String jwt = extractJwtFromHeader(token);
        System.out.println("Deleting activity with id: " + id + " using token: " + jwt);
        activityService.deleteActivity(id, jwt);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/bulk/create")
    public ResponseEntity<List<ActivityResponseDto>> createActivitiesBulk(
            @Valid @RequestBody List<ActivityRequestDto> activities,
            @RequestHeader("Authorization") String token) {
        String jwt = extractJwtFromHeader(token);
        System.out.println("Creating bulk activities, count: " + activities.size());
        List<ActivityResponseDto> responses = activities.stream()
                .map(activity -> activityService.createActivity(activity, jwt))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/bulk/delete")
    public ResponseEntity<Void> deleteActivitiesBulk(
            @RequestBody List<String> activityIds,
            @RequestHeader("Authorization") String token) {
        String jwt = extractJwtFromHeader(token);
        System.out.println("Deleting bulk activities, count: " + activityIds.size());
        activityIds.forEach(id -> activityService.deleteActivity(id, jwt));
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Activity Service is running");
    }

    private String extractJwtFromHeader(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return tokenHeader.substring(7).trim();
        }
        return tokenHeader != null ? tokenHeader.trim() : null;
    }

}
