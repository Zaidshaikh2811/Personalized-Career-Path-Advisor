package com.child1.ai_service.service;


import com.child1.ai_service.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessListner {


    private final GeminiService geminiService;

    @RabbitListener(queues = "activity-queue")
    public void receiveMessage(Activity activity) {
        try {
            System.out.println("Received message: " + activity);
            log.info("Received message: {}", activity);
            System.out.println("Processing activity: " + activity);
            if (activity == null  ) {
                log.error("Activity or Activity ID is null");
                return;
            }
            String prompt = createPromptForActivity(activity);

                    log.info("Generating activity recommendation for prompt: {}", prompt);
            String response = geminiService.getResponse(prompt);
            log.info("Received response from Gemini API: {}", response);
            // Here you can process the response further, e.g., save it to a database or
            // send it to another service.
            log.info("Successfully processed activity message with ID: {}", activity.getId());
        } catch (Exception e) {
            log.error("Error processing activity message: ", e);
            // Optionally: handle the error, send to a dead-letter queue, etc.
        }
    }


    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

}
