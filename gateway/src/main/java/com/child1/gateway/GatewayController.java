package com.child1.gateway;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {


    @GetMapping("/gateway")
    public String gatewayMessage() {
        return "Welcome to the Gateway!";
    }

    // You can add more endpoints as needed for your gateway functionality
}
