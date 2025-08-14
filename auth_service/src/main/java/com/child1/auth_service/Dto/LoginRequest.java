package com.child1.auth_service.Dto;

import com.child1.auth_service.model.UserRole;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginRequest {

    private String email;
    private String password;
    private UserRole role = UserRole.USER;



}