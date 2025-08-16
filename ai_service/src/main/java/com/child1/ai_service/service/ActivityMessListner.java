package com.child1.ai_service.service;


import com.child1.ai_service.model.Activity;
import com.child1.ai_service.model.Recommendation;
import com.child1.ai_service.repo.RecommendationRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessListner {


    private final GeminiService geminiService;

    private final RecommendationRepo recommendationRepo;

    @RabbitListener(queues = "activity-queue")
    public void receiveMessage(Activity activity) {
        if (activity == null) {
            log.error("Activity is null");
            return;
        }
        try {
            String prompt = createPromptForActivity(activity);
            String response = geminiService.getResponse(prompt);
            processResponse(activity, response);
        } catch (Exception e) {
            log.error("Error processing activity message: ", e);
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          \"analysis\": {
            \"overall\": \"Overall analysis here\",
            \"pace\": \"Pace analysis here\",
            \"heartRate\": \"Heart rate analysis here\",
            \"caloriesBurned\": \"Calories analysis here\"
          },
          \"improvements\": [
            {
              \"area\": \"Area name\",
              \"recommendation\": \"Detailed recommendation\"
            }
          ],
          \"suggestions\": [
            {
              \"workout\": \"Workout name\",
              \"description\": \"Detailed workout description\"
            }
          ],
          \"safety\": [
            \"Safety point 1\",
            \"Safety point 2\"
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

    private String processResponse(Activity activity, String response) {
        if (response == null || response.isEmpty()) {
            log.warn("Received empty response for activity ID: {}", activity.getId());
            return "No response from Gemini API";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.path("candidates");
            if (!candidatesNode.isArray() || candidatesNode.isEmpty()) {
                log.warn("No candidates found in response for activity ID: {}", activity.getId());
                return "No candidates found in response";
            }
            JsonNode partsNode = candidatesNode.get(0).path("content").path("parts");
            if (!partsNode.isArray() || partsNode.isEmpty()) {
                log.warn("No parts found in response for activity ID: {}", activity.getId());
                return "No parts found in response";
            }
            String text = partsNode.get(0).path("text").asText("");
            if (text.isEmpty()) {
                log.warn("No text found in Gemini response for activity ID: {}", activity.getId());
                return "No text found in Gemini response";
            }
            // Clean code block markers if present
            String cleanedText = text.trim();
            if (cleanedText.startsWith("```") && cleanedText.endsWith("```") && cleanedText.length() > 6) {
                cleanedText = cleanedText.substring(3, cleanedText.length() - 3).trim();
            }
            int firstBrace = cleanedText.indexOf('{');
            int lastBrace = cleanedText.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                cleanedText = cleanedText.substring(firstBrace, lastBrace + 1);
            }
            JsonNode activityNode = objectMapper.readTree(cleanedText);
            if (activityNode == null) {
                log.warn("No activity data found in response for activity ID: {}", activity.getId());
                return "No activity data found in response";
            }
            // Extract analysis as a readable string
            StringBuilder analysisBuilder = new StringBuilder();
            JsonNode analysisNode = activityNode.path("analysis");
            if (analysisNode.has("overall")) {
                analysisBuilder.append("Overall: ").append(analysisNode.path("overall").asText("")).append("\n");
            }
            if (analysisNode.has("pace")) {
                analysisBuilder.append("Pace: ").append(analysisNode.path("pace").asText("")).append("\n");
            }
            if (analysisNode.has("heartRate")) {
                analysisBuilder.append("Heart Rate: ").append(analysisNode.path("heartRate").asText("")).append("\n");
            }
            if (analysisNode.has("caloriesBurned")) {
                analysisBuilder.append("Calories Burned: ").append(analysisNode.path("caloriesBurned").asText("")).append("\n");
            }
            String analysis = analysisBuilder.toString().trim();
            // Improvements as readable string list
            List<String> improvementsList = new ArrayList<>();
            JsonNode improvementsNode = activityNode.path("improvements");
            if (improvementsNode.isArray()) {
                for (JsonNode imp : improvementsNode) {
                    String area = imp.path("area").asText("");
                    String rec = imp.path("recommendation").asText("");
                    if (!area.isEmpty() || !rec.isEmpty()) {
                        improvementsList.add(area + ": " + rec);
                    }
                }
            }
            // Suggestions as readable string list
            List<String> suggestionsList = new ArrayList<>();
            JsonNode suggestionsNode = activityNode.path("suggestions");
            if (suggestionsNode.isArray()) {
                for (JsonNode sug : suggestionsNode) {
                    String workout = sug.path("workout").asText("");
                    String desc = sug.path("description").asText("");
                    if (!workout.isEmpty() || !desc.isEmpty()) {
                        suggestionsList.add(workout + ": " + desc);
                    }
                }
            }
            // Safety as readable string list
            List<String> safetyList = new ArrayList<>();
            JsonNode safetyNode = activityNode.path("safety");
            if (safetyNode.isArray()) {
                for (JsonNode safe : safetyNode) {
                    String safeText = safe.asText("");
                    if (!safeText.isEmpty()) {
                        safetyList.add(safeText);
                    }
                }
            }


            Recommendation recommendation = new Recommendation();
            System.out.println("Saving recommendation for activity ID: " + activity.getId());
            recommendation.setActivityId(activity.getId());
            recommendation.setUserId(activity.getUserId());
            recommendation.setActivityType(String.valueOf(activity.getActivityType()));
            recommendation.setRecommendationText(analysis);
            recommendation.setImprovements(improvementsList);
            recommendation.setSuggestions(suggestionsList);
            recommendation.setSafety(safetyList);
            recommendationRepo.save(recommendation);

            return String.format("Activity ID: %s processed successfully. Analysis: %s, Improvements: %s, Suggestions: %s, Safety: %s",
                    activity.getId(), analysis, improvementsList, suggestionsList, safetyList);
        } catch (Exception e) {
            log.error("Error processing Gemini response for activity ID: {}", activity.getId(), e);
            return "Error processing Gemini response: " + e.getMessage();
        }
    }

}
