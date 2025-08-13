package com.child1.activity_service.controller;


import com.child1.activity_service.Dto.ActivityRequestDto;
import com.child1.activity_service.Dto.ActivityResponseDto;
import com.child1.activity_service.service.ActivityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService activityService;


    @GetMapping
    public ResponseEntity<List<ActivityResponseDto>> getActivities() {
        System.out.println("Fetching all activities");
        return   ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Boolean> validateUserEmail(@PathVariable String email) {
        System.out.println("Validating user email: " + email);
        return ResponseEntity.ok(activityService.validateUserEmail(email));
    }


    @PostMapping("/create")
    public ResponseEntity<ActivityResponseDto> createActivity(@Valid @RequestBody ActivityRequestDto activity) {
        System.out.println("Creating activity: " + activity);
        return   ResponseEntity.ok(activityService.createActivity(activity));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ActivityResponseDto> updateActivity(@PathVariable Long id,@Valid @RequestBody ActivityRequestDto activity) {
        return ResponseEntity.ok(activityService.updateActivity(id, activity));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {

        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

}
