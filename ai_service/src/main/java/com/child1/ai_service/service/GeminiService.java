package com.child1.ai_service.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {


    private final WebClient webClient;


    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl ;
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getResponse(String prompt) {
        // Gemini API expects a specific JSON structure. Fixing the payload structure.
        Map<String, Object> payload = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );
        System.out.println("Requesting Gemini API with prompt: " + prompt);
        System.out.println("Requesting Gemini API with URL: " + apiUrl + apiKey);
        System.out.println("Payload: " + payload);
        String response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (response == null || response.isEmpty()) {
            return "No response from Gemini API";
        }
        return response;
    }


}
