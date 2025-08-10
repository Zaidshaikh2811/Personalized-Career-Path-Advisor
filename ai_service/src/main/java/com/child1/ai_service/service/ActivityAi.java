package com.child1.ai_service.service;


import com.child1.ai_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAi {

    private final GeminiService geminiService;


    public String generateActivityRecommendation(Activity activity) {
        if (activity == null || activity.getId() == null) {
            log.error("Activity or Activity ID is null");
            return "Invalid activity data provided";
        }
        String prompt = String.format("Generate a recommendation for activity with ID: %s, Type: %s, Duration: %d minutes, Calories Burned: %d, Start Time: %s",
                activity.getId(),
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime() != null ? activity.getStartTime().toString() : " not specified");

        log.info("Generating activity recommendation for prompt: {}", prompt);
        String response = geminiService.getResponse(prompt);
        log.info("Received response from Gemini API: {}", response);
        return response;
    }



}
