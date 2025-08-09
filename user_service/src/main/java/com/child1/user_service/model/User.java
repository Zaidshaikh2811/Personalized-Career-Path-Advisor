package com.child1.user_service.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false, unique = true)
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @jakarta.validation.constraints.NotBlank(message = "First name is required")
    private String firstName;

    @jakarta.validation.constraints.NotBlank(message = "Last name is required")
    private String lastName;

    private UserRole role = UserRole.USER;
    @CreationTimestamp

    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
