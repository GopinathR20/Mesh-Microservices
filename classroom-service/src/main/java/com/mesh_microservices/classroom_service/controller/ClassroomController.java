package com.mesh_microservices.classroom_service.controller;

import com.mesh_microservices.classroom_service.model.*;
import com.mesh_microservices.classroom_service.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * REST controller for managing all classroom-related operations.
 * <p>
 * This class exposes endpoints for creating and managing classrooms, sections,
 * assignments, and learning materials. It delegates all business logic to the
 * {@link ClassroomService}.
 */
@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    /**
     * Creates a new classroom. The creating user's email is required for ownership.
     *
     * @param classroom The Classroom object from the request body.
     * @param facultyEmail The email of the faculty member creating the classroom,
     * passed in the "X-User-Email" request header.
     * @return A ResponseEntity containing the newly created Classroom and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<Classroom> createClassroom(
            @RequestBody Classroom classroom,
            @RequestHeader("X-User-Email") String facultyEmail) {
        Classroom createdClassroom = classroomService.createClassroom(classroom, facultyEmail);
        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single classroom by its unique ID.
     *
     * @param classroomId The ID of the classroom to retrieve.
     * @return A ResponseEntity containing the found Classroom, or a 404 Not Found status.
     */
    @GetMapping("/{classroomId}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable String classroomId) {
        Optional<Classroom> classroom = classroomService.findClassroomById(classroomId);
        return classroom.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Adds a new section to an existing classroom.
     *
     * @param classroomId The ID of the classroom to modify.
     * @param section The Section object to add, from the request body.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return A ResponseEntity containing the updated Classroom.
     */
    @PostMapping("/{classroomId}/sections")
    public ResponseEntity<Classroom> addSection(
            @PathVariable String classroomId,
            @RequestBody Section section,
            @RequestHeader("X-User-Email") String facultyEmail) {
        Classroom updatedClassroom = classroomService.addSection(classroomId, section, facultyEmail);
        return ResponseEntity.ok(updatedClassroom);
    }

    /**
     * Deletes a section from a classroom.
     *
     * @param classroomId The ID of the classroom to modify.
     * @param sectionId The ID of the section to delete.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return A ResponseEntity containing the updated Classroom.
     */
    @DeleteMapping("/{classroomId}/sections/{sectionId}")
    public ResponseEntity<Classroom> deleteSection(
            @PathVariable String classroomId,
            @PathVariable String sectionId,
            @RequestHeader("X-User-Email") String facultyEmail) {

        Classroom updatedClassroom = classroomService.deleteSection(classroomId, sectionId, facultyEmail);
        return ResponseEntity.ok(updatedClassroom);
    }

    /**
     * Creates a new assignment within a classroom.
     *
     * @param classroomId The ID of the classroom where the assignment will be created.
     * @param assignment The Assignment object from the request body.
     * @param facultyEmail The email of the faculty member creating the assignment.
     * @return A ResponseEntity containing the updated Classroom.
     */
    @PostMapping("/{classroomId}/assignments")
    public ResponseEntity<Classroom> createAssignment(
            @PathVariable String classroomId,
            @RequestBody Assignment assignment,
            @RequestHeader("X-User-Email") String facultyEmail) {
        Classroom updatedClassroom = classroomService.createAssignment(classroomId, assignment, facultyEmail);
        return ResponseEntity.ok(updatedClassroom);
    }

    /**
     * Adds learning material (text and/or a file) to a specific section of a classroom.
     * This is a multipart endpoint capable of handling file uploads.
     *
     * @param classroomId The ID of the target classroom.
     * @param sectionId The ID of the target section within the classroom.
     * @param title The title of the material.
     * @param textContent Optional text content for the material.
     * @param file Optional file to be uploaded as part of the material.
     * @param facultyEmail The email of the faculty member adding the material.
     * @return A ResponseEntity containing the updated Classroom.
     * @throws IOException if there is an error during file processing.
     */
    @PostMapping(value = "/{classroomId}/sections/{sectionId}/materials", consumes = {"multipart/form-data"})
    public ResponseEntity<Classroom> addMaterial(
            @PathVariable String classroomId,
            @PathVariable String sectionId,
            @RequestParam String title,
            @RequestParam(required = false) String textContent,
            @RequestParam(required = false) MultipartFile file,
            @RequestHeader("X-User-Email") String facultyEmail) throws IOException {

        Material material = new Material();
        material.setTitle(title);
        material.setTextContent(textContent);

        Classroom updatedClassroom = classroomService.addMaterialToSection(classroomId, sectionId, material, file, facultyEmail);
        return ResponseEntity.ok(updatedClassroom);
    }

    /**
     * Submits a file for an assignment on behalf of a student.
     *
     * @param classroomId The ID of the classroom containing the assignment.
     * @param assignmentId The ID of the assignment being submitted.
     * @param file The file being submitted by the student.
     * @param studentEmail The email of the student submitting the assignment, from the "X-User-Email" header.
     * @return A ResponseEntity containing the updated Classroom, reflecting the new submission.
     * @throws IOException if there is an error during file processing.
     */
    @PostMapping("/{classroomId}/assignments/{assignmentId}/submit")
    public ResponseEntity<Classroom> submitAssignment(
            @PathVariable String classroomId,
            @PathVariable String assignmentId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Email") String studentEmail) throws IOException {

        Classroom updatedClassroom = classroomService.submitAssignment(classroomId, assignmentId, file, studentEmail);
        return ResponseEntity.ok(updatedClassroom);
    }
}