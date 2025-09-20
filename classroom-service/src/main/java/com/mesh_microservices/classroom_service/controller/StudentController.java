package com.mesh_microservices.classroom_service.controller;

import com.mesh_microservices.classroom_service.model.Classroom;
import com.mesh_microservices.classroom_service.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for handling student-specific classroom operations.
 * <p>
 * This class exposes endpoints for students to join classrooms and view the
 * classrooms they are enrolled in.
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private ClassroomService classroomService;

    /**
     * Allows a student to join an existing classroom using a unique classroom code.
     *
     * @param payload A Map containing the "classroomCode".
     * @param studentEmail The email of the student joining, passed in the
     * "X-User-Email" request header.
     * @return A ResponseEntity containing the Classroom that the student has joined.
     */
    @PostMapping("/classrooms/join")
    public ResponseEntity<Classroom> joinClassroom(
            @RequestBody Map<String, String> payload,
            @RequestHeader("X-User-Email") String studentEmail) {
        String classroomCode = payload.get("classroomCode");
        Classroom classroom = classroomService.joinClassroom(classroomCode, studentEmail);
        return ResponseEntity.ok(classroom);
    }

    /**
     * Retrieves a list of all classrooms a particular student is enrolled in.
     *
     * @param studentEmail The email of the student whose classrooms are to be retrieved,
     * passed in the "X-User-Email" request header.
     * @return A ResponseEntity containing a list of the student's classrooms.
     */
    @GetMapping("/classrooms")
    public ResponseEntity<List<Classroom>> getMyClassrooms(
            @RequestHeader("X-User-Email") String studentEmail) {
        List<Classroom> classrooms = classroomService.findClassroomsByStudent(studentEmail);
        return ResponseEntity.ok(classrooms);
    }
}