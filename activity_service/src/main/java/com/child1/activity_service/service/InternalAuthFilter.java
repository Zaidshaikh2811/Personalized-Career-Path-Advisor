package com.child1.activity_service.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Value("${common.internal-secret}")
    private String internalSecret;

    @PostConstruct
    public void init() {
        System.out.println("InternalAuthFilter initialized with internalSecret: " + internalSecret);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String internalHeader = request.getHeader("X-Internal-Auth");
        System.out.println("InternalAuthFilter: internalSecret = " + internalSecret);
        System.out.println("Result:   "+internalSecret.equals(internalHeader));
        if (!internalSecret.equals(internalHeader)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }
}