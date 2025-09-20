package com.mesh_microservices.user_service.controller;

import com.mesh_microservices.user_service.model.User;
import com.mesh_microservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for handling all user-related operations.
 * This class exposes a set of endpoints for user registration, login,
 * and standard CRUD (Create, Read, Update, Delete) functionalities.
 * The base path for all endpoints in this controller is "/users".
 */
@RestController
@RequestMapping("/users")
public class UserController {

    // Injects the UserRepository to interact with the user data in the database.
    @Autowired
    private UserRepository userRepository;


    // === USER REGISTRATION AND LOGIN ===

    /**
     * Handles the registration of a new user.
     * It checks if the email is already in use before creating a new user record.
     * The default role is "student" if not otherwise specified.
     *
     * @param user The User object from the request body containing registration details.
     * @return A string message indicating the result of the registration attempt.
     */
    @PostMapping("/signup")
    public String studentSignup(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "Email already registered";
        }
        user.setRole(user.getRole() != null ? user.getRole().toLowerCase() : "student");
        user.setRegisteredAt(LocalDateTime.now());
        userRepository.save(user);
        return "User registered successfully as " + user.getRole();
    }

    /**
     * Authenticates a user based on their email and password.
     *
     * @param user The User object from the request body containing login credentials.
     * @return A string message indicating whether the login was successful.
     */
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            return "Email not found";
        }
        if (existingUser.getPassword().equals(user.getPassword())) {
            return "Login successful as " + existingUser.getRole();
        } else {
            return "Incorrect password";
        }
    }


    // === ADMIN CRUD OPERATIONS ===

    /**
     * Retrieves a list of all users from the database.
     *
     * @return A list of all User objects.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a single user by their unique user ID.
     *
     * @param userId The unique identifier of the user.
     * @return A ResponseEntity containing the found User or a 404 Not Found status.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a single user by their email address.
     * This endpoint is useful for inter-service communication where services
     * might know a user's email but not their ID.
     *
     * @param email The email address of the user to retrieve.
     * @return A ResponseEntity containing the found User or a 404 Not Found status.
     */
    @GetMapping("/byEmail/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates the details of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param userDetails A User object from the request body with the updated information.
     * @return A ResponseEntity containing the updated User or a 404 Not Found status.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User userDetails) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setRole(userDetails.getRole());
                    // Note: Add other fields to be updated here as necessary.
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a user from the database by their ID.
     *
     * @param userId The ID of the user to be deleted.
     * @return An empty ResponseEntity with a 200 OK status on success, or a 404 Not Found status.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}