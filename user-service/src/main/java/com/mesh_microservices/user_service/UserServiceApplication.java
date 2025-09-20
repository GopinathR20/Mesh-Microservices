package com.mesh_microservices.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the User Service microservice.
 * <p>
 * This class contains the main method which bootstraps the Spring Boot application.
 * The {@code @SpringBootApplication} annotation enables auto-configuration, component scanning,
 * and other key Spring Boot features.
 */
@SpringBootApplication
public class UserServiceApplication {

	/**
	 * The main method that launches the User Service application.
	 *
	 * @param args Command-line arguments passed to the application at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}