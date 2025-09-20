package com.mesh_microservices.admin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * The main entry point for the Admin Service microservice.
 * <p>
 * This class boots the Spring application and, critically, configures the
 * {@link RestTemplate} bean required for communicating with other services.
 */
@SpringBootApplication
public class AdminServiceApplication {

	/**
	 * The main method that launches the Admin Service application.
	 *
	 * @param args Command-line arguments passed to the application at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}

	/**
	 * Creates a bean for RestTemplate to be used for making REST calls.
	 * <p>
	 * The {@code @LoadBalanced} annotation is the key to enabling client-side
	 * load balancing. It tells Spring Cloud to intercept requests made with this
	 * RestTemplate. When a request is made to a logical service name
	 * (e.g., "http://user-service/users"), it automatically queries the Eureka
	 * discovery server to resolve the name to a running instance's actual
	 * host and port.
	 *
	 * @return A load-balanced RestTemplate instance managed by Spring.
	 */
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}