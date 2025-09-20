package com.mesh_microservices.admin_service.service;

import com.mesh_microservices.admin_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Service class containing the business logic for administrative operations.
 * This service acts as a REST client, making HTTP requests to the 'user-service'
 * to perform CRUD operations on user data.
 */
@Service
public class AdminService {

    // Spring's synchronous client for making HTTP requests to other microservices.
    @Autowired
    private RestTemplate restTemplate;

    // The base URL for the user-service. The 'user-service' hostname is a
    // service ID that will be resolved by the Eureka discovery server to a
    // specific service instance's IP and port.
    private final String USER_SERVICE_URL = "http://user-service/users";

    /**
     * Sends a POST request to the user-service to create a new user.
     *
     * @param user The User object containing the details for the new user.
     * @return A success or error message received from the user-service.
     */
    public String createUser(User user) {
        ResponseEntity<String> response = restTemplate.postForEntity(USER_SERVICE_URL + "/signup", user, String.class);
        return handleResponse(response);
    }

    /**
     * Sends a GET request to the user-service to retrieve a list of all users.
     * Uses 'exchange' to handle the generic type 'List<User>'.
     *
     * @return A List of User objects.
     */
    public List<User> getAllUsers() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
                USER_SERVICE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );
        return response.getBody();
    }

    /**
     * Sends a GET request to the user-service to retrieve a single user by their ID.
     *
     * @param userId The unique ID of the user to fetch.
     * @return The User object if found. RestTemplate will throw an exception for non-2xx responses.
     */
    public User getUserById(String userId) {
        return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, User.class);
    }

    /**
     * Sends a PUT request to the user-service to update an existing user's details.
     *
     * @param userId The ID of the user to update.
     * @param userDetails The User object with the updated information.
     * @return A static confirmation message indicating the request was sent.
     */
    public String updateUser(String userId, User userDetails) {
        restTemplate.put(USER_SERVICE_URL + "/" + userId, userDetails);
        return "User updated successfully.";
    }

    /**
     * Sends a DELETE request to the user-service to remove a user.
     *
     * @param userId The ID of the user to delete.
     * @return A static confirmation message indicating the request was sent.
     */
    public String deleteUser(String userId) {
        restTemplate.delete(USER_SERVICE_URL + "/" + userId);
        return "User deleted successfully.";
    }

    /**
     * A private helper method to process the ResponseEntity from a REST call.
     * It checks for a successful HTTP status code (2xx).
     *
     * @param responseEntity The ResponseEntity object from the RestTemplate call.
     * @return The response body if the call was successful.
     * @throws RuntimeException if the status code is not in the 2xx range, indicating an error.
     */
    private String handleResponse(ResponseEntity<String> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            // In a real application, a more specific custom exception would be better.
            throw new RuntimeException("Operation failed. Status: " + responseEntity.getStatusCode());
        }
    }
}