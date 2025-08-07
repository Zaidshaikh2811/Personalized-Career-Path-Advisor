package com.child1.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {



     @GetMapping("/home")
     public String home() {
         return "Welcome to the backend!";
     }

     @GetMapping("/health")
     public String health() {
            return "Backend is running!";
        }



}
