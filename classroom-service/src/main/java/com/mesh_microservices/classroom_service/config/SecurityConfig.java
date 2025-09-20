package com.mesh_microservices.classroom_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures the security settings for the Classroom Service.
 * <p>
 * This configuration disables CSRF protection and permits all incoming requests
 * to any endpoint within this microservice. This is a common setup for services
 * that sit behind an API Gateway, where the gateway might handle the primary
 * security checks.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain for all HTTP requests.
     *
     * @param http The {@link HttpSecurity} object to be configured.
     * @return The configured {@link SecurityFilterChain} instance.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable Cross-Site Request Forgery (CSRF) protection.
                // This is standard practice for stateless REST APIs that are not browser-based.
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules for HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allow all requests to any endpoint in this service without authentication.
                        .anyRequest().permitAll());

        return http.build();
    }
}