package com.mesh_microservices.classroom_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a student's submission for a specific assignment.
 * <p>
 * This class is a POJO intended to be part of a list within an
 * {@link Assignment} document.
 */
public class Submission {

    /**
     * A unique identifier for this specific submission.
     */
    private String id = UUID.randomUUID().toString();

    /**
     * The unique ID of the student who made the submission.
     */
    private String studentId;

    /**
     * The name of the student who made the submission.
     */
    private String studentName;

    /**
     * The URL pointing to the submitted file, likely stored in cloud storage.
     */
    private String submittedFileUrl;

    /**
     * The timestamp when the assignment was submitted.
     */
    private LocalDateTime submittedAt = LocalDateTime.now();

    /**
     * The grade assigned by the faculty. Can be null until graded.
     */
    private Integer grade;

    /**
     * Feedback provided by the faculty. Can be null until graded.
     */
    private String feedback;

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubmittedFileUrl() {
        return submittedFileUrl;
    }

    public void setSubmittedFileUrl(String submittedFileUrl) {
        this.submittedFileUrl = submittedFileUrl;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}