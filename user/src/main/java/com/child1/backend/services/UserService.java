package com.child1.backend.services;


import com.child1.backend.model.User;
import com.child1.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Service

public class UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User createUser(User user) {

        user.setCreatedDate(java.time.LocalDateTime.now());
        user.setUpdatedDate(java.time.LocalDateTime.now());


        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElse(null);

    }

    public User updateUser(Long id, @Valid User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return null;
        }
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setRole(user.getRole());
        existingUser.setUpdatedDate(java.time.LocalDateTime.now());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }



    }
}
