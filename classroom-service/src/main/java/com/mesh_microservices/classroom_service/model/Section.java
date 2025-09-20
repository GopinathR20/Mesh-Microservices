package com.mesh_microservices.classroom_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a section within a classroom, used to group related materials.
 * <p>
 * This class is a POJO intended to be embedded as part of a list within a
 * {@link Classroom} document.
 */
public class Section {

    /**
     * A unique identifier for the section, generated upon creation.
     */
    private String id = UUID.randomUUID().toString();

    /**
     * The title of the section (e.g., "Week 1: Introduction to Microservices").
     */
    private String title;

    /**
     * A list of learning materials contained within this section.
     */
    private List<Material> materials = new ArrayList<>();

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

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}