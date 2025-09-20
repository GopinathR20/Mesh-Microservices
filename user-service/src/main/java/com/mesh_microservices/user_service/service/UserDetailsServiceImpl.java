package com.mesh_microservices.user_service.service;

import com.mesh_microservices.user_service.model.User;
import com.mesh_microservices.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Collections;

/**
 * A service that implements Spring Security's UserDetailsService.
 * This class is responsible for loading user-specific data from the database
 * during the authentication process.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Injects the UserRepository to fetch user data from the database.
    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username (in this case, the email address).
     * This method is called by Spring Security's authentication manager.
     *
     * @param email The email address of the user to load.
     * @return A UserDetails object that Spring Security can use for authentication and authorization.
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the database using the provided email.
        // If no user is found, throw an exception as required by the UserDetailsService interface.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Create and return a Spring Security User object.
        // This object contains the essential information for the authentication process.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                // Map the user's role to a Spring Security GrantedAuthority.
                // It's a standard convention to prefix roles with "ROLE_".
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
        );
    }
}