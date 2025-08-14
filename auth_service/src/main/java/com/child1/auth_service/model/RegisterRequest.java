package com.child1.auth_service.model;



import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String firstName;
    private String lastName;
}
