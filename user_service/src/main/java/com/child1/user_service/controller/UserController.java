package com.child1.user_service.controller;


import com.child1.user_service.Dto.RegisterRequest;
import com.child1.user_service.Dto.UserResponseDto;
import com.child1.user_service.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor

public class UserController {

    private UserService userService;


    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        System.out.println("Fetching all users");
        return ResponseEntity.ok(  userService.getUsers()  );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        System.out.println("Fetching user by email: " + email);
        return ResponseEntity.ok(
                userService.getUserByEmail(email)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody RegisterRequest userResponseDto ) {
        System.out.println("Creating user with email: " + userResponseDto.getEmail());
        return ResponseEntity.ok(userService.createUser(userResponseDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println("Updating user with ID: " + id);
        return ResponseEntity.ok(
                userService.updateUser(id)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.deleteUser(id)
        );
    }







}
