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
    public UserResponseDto createUser(RegisterRequest userResponseDto) {
        // Check if user with email already exists
        if (userRepository.existsByEmail(userResponseDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        // Additional checks can be added here (e.g., password strength, name format, etc.)

        User user = new User();
        user.setName(userResponseDto.getName());
        user.setEmail(userResponseDto.getEmail());
        user.setPassword(userResponseDto.getPassword());
        user.setFirstName(userResponseDto.getFirstName());
        user.setLastName(userResponseDto.getLastName());

         User savedUser=userRepository.save(user);

        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setName(savedUser.getName());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setFirstName(savedUser.getFirstName());
        responseDto.setLastName(savedUser.getLastName());
        responseDto.setRole(savedUser.getRole());

        return responseDto;

    }

     // Example method to update a user
    public UserResponseDto updateUser(Long id) {

        // Logic to update a user by id
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Update user fields as needed
        User updatedUser = userRepository.save(user);

        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setName(updatedUser.getName());
        responseDto.setEmail(updatedUser.getEmail());
        responseDto.setFirstName(updatedUser.getFirstName());
        responseDto.setLastName(updatedUser.getLastName());
        responseDto.setRole(updatedUser.getRole());

        return responseDto;

    }

    // Example method to delete a user
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setName(user.getName());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setRole(user.getRole());
        return responseDto;
    }

    // Example method to get all users
    public List<UserResponseDto> getUsers() {

        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserResponseDto responseDto = new UserResponseDto();

            responseDto.setName(user.getName());
            responseDto.setEmail(user.getEmail());
            responseDto.setFirstName(user.getFirstName());
            responseDto.setLastName(user.getLastName());
            responseDto.setRole(user.getRole());
            return responseDto;
        }).toList();
    }

}
