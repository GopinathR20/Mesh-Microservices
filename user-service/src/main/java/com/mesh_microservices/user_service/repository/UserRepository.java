package com.mesh_microservices.user_service.repository;

import com.mesh_microservices.user_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * A Spring Data repository for managing User entities in the MongoDB database.
 *
 * This interface extends MongoRepository, which provides a full set of standard
 * CRUD (Create, Read, Update, Delete) operations for the User model.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a user by their unique email address.
     * Spring Data MongoDB automatically implements this method based on its name.
     *
     * @param email The email address of the user to find.
     * @return An Optional containing the found user, or an empty Optional if no user
     * with the specified email exists.
     */
    Optional<User> findByEmail(String email);
}