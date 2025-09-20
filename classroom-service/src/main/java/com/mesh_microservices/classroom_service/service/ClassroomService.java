package com.mesh_microservices.classroom_service.service;

import com.mesh_microservices.classroom_service.model.*;
import com.mesh_microservices.classroom_service.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The primary service class for the classroom-service.
 * <p>
 * This class contains all the business logic for creating, managing, and interacting
 * with classrooms. It coordinates with the {@link ClassroomRepository} for database
 * operations, the {@link AzureBlobService} for file storage, and uses a
 * {@link RestTemplate} to communicate with the user-service.
 */
@Service
public class ClassroomService {

    // Handles database operations for Classroom entities.
    @Autowired
    private ClassroomRepository classroomRepository;

    // Used for making REST calls to other microservices (e.g., user-service).
    @Autowired
    private RestTemplate restTemplate;

    // Handles file upload logic to Azure Blob Storage.
    @Autowired
    private AzureBlobService azureBlobService;

    // Constants for generating unique classroom codes.
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * A private helper method to fetch user details from the user-service by email.
     *
     * @param email The email of the user to retrieve.
     * @return The User DTO containing the user's details.
     * @throws IllegalArgumentException if no user is found with the given email.
     */
    private User getUserByEmail(String email) {
        // The "user-service" name is resolved by Eureka to an actual host and port.
        String url = "http://user-service/users/byEmail/" + email;
        User user = restTemplate.getForObject(url, User.class);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return user;
    }

    /**
     * Creates a new classroom, assigning ownership to the specified faculty member.
     *
     * @param classroom The basic classroom object to be created.
     * @param facultyEmail The email of the faculty member creating the classroom.
     * @return The saved Classroom entity, complete with a unique ID, faculty info, and join code.
     */
    public Classroom createClassroom(Classroom classroom, String facultyEmail) {
        User faculty = getUserByEmail(facultyEmail);

        classroom.setFacultyId(faculty.getId());
        classroom.setFacultyName(faculty.getName());
        classroom.setCreatedAt(LocalDateTime.now());
        classroom.setClassroomCode(generateUniqueCode());

        return classroomRepository.save(classroom);
    }

    /**
     * Generates a 6-character unique alphanumeric code for joining a classroom.
     * It ensures the code is not already in use by checking the database.
     *
     * @return A unique, 6-character string.
     */
    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
            }
            code = sb.toString();
        } while (classroomRepository.findByClassroomCode(code).isPresent());
        return code;
    }

    /**
     * A private helper to retrieve a classroom and verify that the requesting
     * user is the faculty member who owns it.
     *
     * @param classroomId The ID of the classroom to find.
     * @param facultyEmail The email of the user attempting the action.
     * @return The Classroom object if found and ownership is verified.
     * @throws RuntimeException if the classroom is not found.
     * @throws SecurityException if the user is not the owner.
     */
    private Classroom findClassroomAndVerifyFaculty(String classroomId, String facultyEmail) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with ID: " + classroomId));

        User faculty = getUserByEmail(facultyEmail);

        if (!classroom.getFacultyId().equals(faculty.getId())) {
            throw new SecurityException("You are not authorized to modify this classroom.");
        }
        return classroom;
    }

    /**
     * Adds a new section to a classroom after verifying faculty ownership.
     *
     * @param classroomId The ID of the classroom to modify.
     * @param section The new Section to add.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return The updated Classroom entity.
     */
    public Classroom addSection(String classroomId, Section section, String facultyEmail) {
        Classroom classroom = findClassroomAndVerifyFaculty(classroomId, facultyEmail);
        classroom.getSections().add(section);
        return classroomRepository.save(classroom);
    }

    /**
     * Adds learning material to a specific section within a classroom.
     * If a file is provided, it's uploaded; otherwise, it's treated as text-only.
     *
     * @param classroomId The ID of the classroom.
     * @param sectionId The ID of the section to add the material to.
     * @param material The material object (title, text content).
     * @param file The optional file to upload.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return The updated Classroom entity.
     * @throws IOException if there is a file upload error.
     */
    public Classroom addMaterialToSection(String classroomId, String sectionId, Material material, MultipartFile file, String facultyEmail) throws IOException {
        Classroom classroom = findClassroomAndVerifyFaculty(classroomId, facultyEmail);

        Section section = classroom.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Section not found with ID: " + sectionId));

        if (file != null && !file.isEmpty()) {
            String fileUrl = azureBlobService.uploadFile(file);
            material.setType(Material.MaterialType.FILE);
            material.setFileUrl(fileUrl);
        } else {
            material.setType(Material.MaterialType.TEXT);
        }

        section.getMaterials().add(material);
        return classroomRepository.save(classroom);
    }

    /**
     * Creates a new assignment in a classroom after verifying faculty ownership.
     *
     * @param classroomId The ID of the classroom.
     * @param assignment The new Assignment to add.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return The updated Classroom entity.
     */
    public Classroom createAssignment(String classroomId, Assignment assignment, String facultyEmail) {
        Classroom classroom = findClassroomAndVerifyFaculty(classroomId, facultyEmail);
        classroom.getAssignments().add(assignment);
        return classroomRepository.save(classroom);
    }

    /**
     * Allows a student to join a classroom using a valid join code.
     *
     * @param classroomCode The 6-character code of the classroom to join.
     * @param studentEmail The email of the student joining.
     * @return The updated Classroom entity.
     * @throws RuntimeException if the classroom code is not found.
     * @throws IllegalArgumentException if the student is already enrolled.
     */
    public Classroom joinClassroom(String classroomCode, String studentEmail) {
        User student = getUserByEmail(studentEmail);

        Classroom classroom = classroomRepository.findByClassroomCode(classroomCode)
                .orElseThrow(() -> new RuntimeException("Classroom with code '" + classroomCode + "' not found."));

        if (classroom.getStudentIds().contains(student.getId())) {
            throw new IllegalArgumentException("Student is already enrolled in this classroom.");
        }

        classroom.getStudentIds().add(student.getId());
        return classroomRepository.save(classroom);
    }

    /**
     * Finds all classrooms that a specific student is enrolled in.
     *
     * @param studentEmail The email of the student.
     * @return A list of classrooms the student has joined.
     */
    public List<Classroom> findClassroomsByStudent(String studentEmail) {
        User student = getUserByEmail(studentEmail);
        return classroomRepository.findByStudentIdsContains(student.getId());
    }

    /**
     * Processes an assignment submission from a student.
     * It verifies enrollment, uploads the file, and adds the submission record.
     * Allows for re-submission by replacing the previous submission.
     *
     * @param classroomId The ID of the classroom.
     * @param assignmentId The ID of the assignment.
     * @param file The file being submitted.
     * @param studentEmail The email of the submitting student.
     * @return The updated Classroom entity with the new submission.
     * @throws IOException if there is a file upload error.
     * @throws SecurityException if the student is not enrolled in the class.
     */
    public Classroom submitAssignment(String classroomId, String assignmentId, MultipartFile file, String studentEmail) throws IOException {
        User student = getUserByEmail(studentEmail);

        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found."));

        if (!classroom.getStudentIds().contains(student.getId())) {
            throw new SecurityException("You are not enrolled in this classroom.");
        }

        Assignment assignment = classroom.getAssignments().stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Assignment not found."));

        String fileUrl = azureBlobService.uploadFile(file);

        Submission newSubmission = new Submission();
        newSubmission.setStudentId(student.getId());
        newSubmission.setStudentName(student.getName());
        newSubmission.setSubmittedFileUrl(fileUrl);
        newSubmission.setSubmittedAt(LocalDateTime.now());

        // Remove any previous submission from the same student to allow resubmission.
        assignment.getSubmissions().removeIf(sub -> sub.getStudentId().equals(student.getId()));
        assignment.getSubmissions().add(newSubmission);

        return classroomRepository.save(classroom);
    }

    /**
     * Finds a classroom by its unique ID.
     *
     * @param classroomId The ID of the classroom to find.
     * @return An Optional containing the Classroom if found, otherwise empty.
     */
    public Optional<Classroom> findClassroomById(String classroomId) {
        return classroomRepository.findById(classroomId);
    }

    /**
     * Deletes a section from a classroom after verifying faculty ownership.
     *
     * @param classroomId The ID of the classroom to modify.
     * @param sectionId The ID of the section to be deleted.
     * @param facultyEmail The email of the faculty member performing the action.
     * @return The updated Classroom entity.
     * @throws RuntimeException if the section is not found.
     */
    public Classroom deleteSection(String classroomId, String sectionId, String facultyEmail) {
        Classroom classroom = findClassroomAndVerifyFaculty(classroomId, facultyEmail);

        boolean removed = classroom.getSections().removeIf(section -> section.getId().equals(sectionId));

        if (!removed) {
            throw new RuntimeException("Section not found with ID: " + sectionId);
        }

        return classroomRepository.save(classroom);
    }

}