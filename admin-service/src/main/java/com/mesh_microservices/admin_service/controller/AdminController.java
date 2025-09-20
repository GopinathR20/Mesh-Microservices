package com.mesh_microservices.admin_service.controller;

import com.mesh_microservices.admin_service.model.User;
import com.mesh_microservices.admin_service.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

/**
 * REST controller for handling administrative operations on users.
 * This controller exposes endpoints for creating, retrieving, updating, and deleting
 * user accounts. It delegates the business logic to the AdminService.
 * The base path for all endpoints is "/api/admin/users".
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    // Injects the service layer which contains the business logic for admin operations.
    @Autowired
    private AdminService adminService;

    /**
     * Creates a new user in the system.
     *
     * @param newUser A User object from the request body containing the new user's details.
     * @return A ResponseEntity with a success message and HTTP status 201 (Created),
     * or an error message with a relevant HTTP status on failure.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User newUser) {
        try {
            String responseMessage = adminService.createUser(newUser);
            return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A ResponseEntity containing a list of all User objects with HTTP status 200 (OK),
     * or an error message on failure.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a single user by their unique ID.
     *
     * @param userId The ID of the user to retrieve, passed as a path variable.
     * @return A ResponseEntity containing the found User object with HTTP status 200 (OK),
     * or an error message with HTTP status 404 (Not Found) if the user doesn't exist.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            User user = adminService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (HttpClientErrorException.NotFound e) {
            return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the details of an existing user.
     *
     * @param userId The ID of the user to update.
     * @param userDetails A User object from the request body with the updated information.
     * @return A ResponseEntity with a success message and HTTP status 200 (OK),
     * or an error message with HTTP status 404 (Not Found) if the user doesn't exist.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody User userDetails) {
        try {
            String responseMessage = adminService.updateUser(userId, userDetails);
            return ResponseEntity.ok(responseMessage);
        } catch (HttpClientErrorException.NotFound e) {
            return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a user from the system by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return A ResponseEntity with a success message and HTTP status 200 (OK),
     * or an error message with HTTP status 404 (Not Found) if the user doesn't exist.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            String responseMessage = adminService.deleteUser(userId);
            return ResponseEntity.ok(responseMessage);
        } catch (HttpClientErrorException.NotFound e) {
            return new ResponseEntity<>("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}