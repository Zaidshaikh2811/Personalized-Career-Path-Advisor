package com.child1.user_service.Service;


import com.child1.user_service.Dto.RegisterRequest;
import com.child1.user_service.Dto.UserResponseDto;
import com.child1.user_service.Repo.UserRepository;
import com.child1.user_service.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {


    private UserRepository userRepository;



    // Example method to create a user
    public UserResponseDto createUser(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("User data must not be null");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());


        User savedUser = userRepository.save(user);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setName(savedUser.getName());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setFirstName(savedUser.getFirstName());
        responseDto.setLastName(savedUser.getLastName());


        return responseDto;

    }

     // Example method to update a user
    public UserResponseDto updateUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Update user fields as needed
        User updatedUser = userRepository.save(user);

        return getUserResponseDto(updatedUser);

    }

    // Example method to delete a user
    public UserResponseDto deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return getUserResponseDto(user);
    }

    // Example method to get all users
    public List<UserResponseDto> getUsers() {

        List<User> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("No users found");
        }
        return users.stream().map(user -> {
            return getUserResponseDto(user);
        }).toList();
    }

    public UserResponseDto getUserById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return getUserResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with the provided email");
        }
        return getUserResponseDto(user);
    }

    private UserResponseDto getUserResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setName(user.getName());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        return responseDto;
    }
}
