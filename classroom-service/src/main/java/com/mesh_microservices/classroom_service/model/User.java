package com.mesh_microservices.classroom_service.model;

/**
 * A lightweight Data Transfer Object (DTO) to hold basic user information.
 * <p>
 * This class is used within the classroom-service to represent a user (student
 * or faculty) without needing all the details from the full User model in the
 * user-service. It's primarily used for inter-service communication.
 */
public class User {

    /**
     * The user's unique identifier.
     */
    private String id;

    /**
     * The user's full name.
     */
    private String name;

    /**
     * The user's unique email address.
     */
    private String email;

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}