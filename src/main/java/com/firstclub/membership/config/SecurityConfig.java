package com.firstclub.membership.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for password encoding
 * 
 * This configuration provides a production-ready password encoder
 * using BCrypt hashing algorithm for secure password storage.
 */
@Configuration
public class SecurityConfig {

    /**
     * Password encoder bean using BCrypt
     * 
     * BCrypt is a secure hashing function designed for passwords
     * with built-in salt generation and configurable work factor.
     * 
     * @return BCryptPasswordEncoder instance with default strength (10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 for production security
    }
}
