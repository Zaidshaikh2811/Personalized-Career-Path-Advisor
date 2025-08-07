package com.child1.backend.controller;


import com.child1.backend.model.User;
import com.child1.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final  UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


     @GetMapping
     public ResponseEntity<List<User>> getAllUsers() {
         List<User> users = userService.getAllUsers();
         return new ResponseEntity<>(users, HttpStatus.OK);
     }


     @PostMapping
     public ResponseEntity<User> createUser(@Valid @RequestBody User user) {

         User createdUser = userService.createUser(user);
         return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
     }

     @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
     }

        @PutMapping("/{id}")
        public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {

            User updatedUser = userService.updateUser(id, user);
            if (updatedUser != null) {
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

            User existingUser = userService.findById(id);
            if (existingUser != null) {
                userService.deleteUser(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }

}
