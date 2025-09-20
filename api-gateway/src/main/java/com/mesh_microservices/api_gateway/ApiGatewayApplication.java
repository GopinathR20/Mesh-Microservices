package com.mesh_microservices.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Spring Cloud API Gateway microservice.
 * <p>
 * This class contains the main method which bootstraps the entire gateway application.
 * The API Gateway is responsible for routing all incoming client requests to the
 * appropriate downstream microservices.
 */
@SpringBootApplication
public class ApiGatewayApplication {

	/**
	 * The main method that launches the API Gateway application.
	 *
	 * @param args Command-line arguments passed to the application at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}