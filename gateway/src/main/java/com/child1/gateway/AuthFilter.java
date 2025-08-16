package com.child1.gateway;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Request path: " + path);
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", "Authorization token is required");
        }

        String token = authHeader.substring(7);


        System.out.println("Extracted token: " + token);
        if (token.isEmpty()) {
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", "Token is empty");
        }
        System.out.println("Validating token: " + token);
        return webClientBuilder.build()
                .get()
                .uri("http://auth-service/api/v1/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Map.class)
                                .flatMap(body -> {
                                    Boolean isValid = (Boolean) body.get("valid");
                                    if (Boolean.TRUE.equals(isValid)) {
                                        return chain.filter(exchange);
                                    }
                                    return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid token");
                                });
                    } else {
                        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", "Token validation failed");
                    }
                });
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String error, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().set("Content-Type", "application/json");
        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", error,
                "message", message
        );
        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

@Configuration
class WebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
