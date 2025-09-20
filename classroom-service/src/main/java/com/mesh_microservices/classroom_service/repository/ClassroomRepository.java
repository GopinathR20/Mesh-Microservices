package com.mesh_microservices.classroom_service.repository;

import com.mesh_microservices.classroom_service.model.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

/**
 * A Spring Data repository for managing {@link Classroom} entities in MongoDB.
 * <p>
 * This interface extends {@link MongoRepository}, which provides standard CRUD
 * (Create, Read, Update, Delete) operations for the Classroom model out of the box.
 * Custom query methods are also defined here.
 */
public interface ClassroomRepository extends MongoRepository<Classroom, String> {

    /**
     * Finds a classroom by its unique, shareable classroom code.
     * <p>
     * Spring Data MongoDB automatically implements this method based on its name.
     *
     * @param classroomCode The unique code used by students to join a classroom.
     * @return An {@link Optional} containing the found classroom, or an empty
     * Optional if no classroom with the specified code exists.
     */
    Optional<Classroom> findByClassroomCode(String classroomCode);

    /**
     * Finds all classrooms that a specific student is enrolled in.
     * <p>
     * This query works by checking if the provided studentId is present in the
     * {@code studentIds} list within the Classroom document.
     *
     * @param studentId The unique ID of the student.
     * @return A {@link List} of classrooms that the student has joined. The list
     * will be empty if the student is not enrolled in any classrooms.
     */
    List<Classroom> findByStudentIdsContains(String studentId);
}