package com.mesh_microservices.classroom_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * The main entry point for the Classroom Service microservice.
 * <p>
 * This class boots the Spring application and configures beans required for
 * its operation, such as the load-balanced {@link RestTemplate} for
 * inter-service communication.
 */
@SpringBootApplication
public class ClassroomServiceApplication {

	/**
	 * The main method that launches the Classroom Service application.
	 *
	 * @param args Command-line arguments passed to the application at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ClassroomServiceApplication.class, args);
	}

	/**
	 * Creates a bean for RestTemplate to be used for making REST calls to other services.
	 * <p>
	 * The {@code @LoadBalanced} annotation is the key to enabling client-side
	 * load balancing. It integrates this RestTemplate with the service discovery
	 * (Eureka) mechanism. When a request is made to a logical service name
	 * (e.g., "http://user-service/users"), it will automatically resolve the name
	 * to a running instance's actual host and port.
	 *
	 * @return A load-balanced RestTemplate instance managed by Spring.
	 */
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}