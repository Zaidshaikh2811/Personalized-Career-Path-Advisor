package com.child1.user_service.Dto;


import com.child1.user_service.model.UserRole;

import lombok.Data;


import java.time.LocalDateTime;


@Data
public class UserResponseDto {

    private String name;

     private String email;

     private String firstName;

    private String lastName;



}
