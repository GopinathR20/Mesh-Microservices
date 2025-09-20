package com.mesh_microservices.classroom_service.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a single assignment within a classroom.
 * <p>
 * This class is a Plain Old Java Object (POJO) that is not a top-level
 * MongoDB document but is intended to be part of a list within a
 * {@link Classroom} document.
 */
public class Assignment {

    /**
     * A unique identifier for the assignment, generated upon creation.
     */
    private String id = UUID.randomUUID().toString();

    /**
     * The title of the assignment.
     */
    private String title;

    /**
     * The detailed description or instructions for the assignment.
     */
    private String description;

    /**
     * The maximum points or grade a student can receive for this assignment.
     */
    private int points;

    /**
     * The timestamp when the assignment was created and posted.
     */
    private LocalDateTime postedAt = LocalDateTime.now();

    /**
     * The deadline for submitting the assignment.
     */
    private LocalDateTime dueDate;

    /**
     * A list of submissions made by students for this assignment.
     */
    private List<Submission> submissions = new ArrayList<>();

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

}