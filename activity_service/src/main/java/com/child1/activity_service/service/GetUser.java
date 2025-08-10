package com.child1.activity_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GetUser {

    private final WebClient webClient;


    public String getUserByEmail(String email) {
        try{


        return webClient.
                get()
                .uri("/api/v1/users/email/{email}", email)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    catch (Exception e) {
            throw new RuntimeException("User not found with the provided email: " + email, e);
        }
}
}
