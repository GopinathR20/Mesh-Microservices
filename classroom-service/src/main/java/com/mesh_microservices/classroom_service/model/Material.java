package com.mesh_microservices.classroom_service.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a piece of learning material within a classroom section.
 * <p>
 * A material can be a simple text note or a link to an uploaded file.
 * This class is intended to be embedded within a {@link Section} object.
 */
public class Material {

    /**
     * Defines the type of the material.
     */
    public enum MaterialType {
        /**
         * The material consists only of text content.
         */
        TEXT,
        /**
         * The material is a file that has been uploaded.
         */
        FILE
    }

    /**
     * A unique identifier for the material, generated upon creation.
     */
    private String id = UUID.randomUUID().toString();

    /**
     * The title of the learning material.
     */
    private String title;

    /**
     * The type of the material (TEXT or FILE).
     */
    private MaterialType type;

    /**
     * The content for text-only materials. Null if the material is a file.
     */
    private String textContent;

    /**
     * The URL pointing to the uploaded file. Null if the material is text-only.
     */
    private String fileUrl;

    /**
     * The timestamp when the material was added or uploaded.
     */
    private LocalDateTime uploadedAt = LocalDateTime.now();

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

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}