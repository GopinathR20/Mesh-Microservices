package com.mesh_microservices.discovery_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main application class for the Eureka Discovery Server.
 * This service acts as a registry for all other microservices within the application ecosystem.
 * The @EnableEurekaServer annotation configures this Spring Boot application to function as a service registry.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

	/**
	 * The main entry point for the Spring Boot application.
	 * This method bootstraps and launches the Discovery Server.
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DiscoveryServerApplication.class, args);
	}

}