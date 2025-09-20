package com.mesh_microservices.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures the web security for the User Service.
 * <p>
 * This class uses Spring Security to define access rules for the application's endpoints.
 * The current configuration is intentionally permissive, allowing all traffic without
 * authentication. This is suitable for development or for services where security
 * is handled at a higher level, such as by an API Gateway.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain that applies to all HTTP requests.
     * <p>
     * This bean configures the core security policies, such as disabling CSRF and
     * setting up authorization rules for all endpoints.
     *
     * @param http The {@link HttpSecurity} object to be configured.
     * @return The configured {@link SecurityFilterChain} instance.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable Cross-Site Request Forgery (CSRF) protection. This is standard
                // practice for stateless REST APIs that are not called directly from a browser.
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization rules for incoming requests.
                .authorizeHttpRequests(auth -> auth
                        // This rule permits all requests to any endpoint without requiring
                        // any authentication or authorization.
                        .anyRequest().permitAll());

        // Build and return the configured SecurityFilterChain.
        return http.build();
    }
}