package com.firstclub.membership.unit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BCrypt password encoding functionality
 */
@SpringBootTest
public class BCryptPasswordEncodingTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testBCryptPasswordEncodingStrength() {
        // Test that BCrypt is properly configured with high strength
        String testPassword = "TestPassword123!";
        String encodedPassword = passwordEncoder.encode(testPassword);
        
        // BCrypt hashes start with $2a$, $2b$, or $2y$ and contain strength
        assertThat(encodedPassword).startsWith("$2");
        assertThat(encodedPassword).hasSize(60); // Standard BCrypt hash length
        
        // Verify password matches
        assertThat(passwordEncoder.matches(testPassword, encodedPassword)).isTrue();
        
        // Verify different password doesn't match
        assertThat(passwordEncoder.matches("WrongPassword", encodedPassword)).isFalse();
    }

    @Test
    public void testBCryptStrengthConfiguration() {
        // Test that the encoder is using strength 12 by checking encoding time
        String testPassword = "ComplexPassword123!@#";
        
        long startTime = System.currentTimeMillis();
        String encodedPassword = passwordEncoder.encode(testPassword);
        long encodingTime = System.currentTimeMillis() - startTime;
        
        // With strength 12, encoding should take significant time (but not too long for tests)
        assertThat(encodingTime).isGreaterThan(50); // At least 50ms for strength 12
        assertThat(encodingTime).isLessThan(5000); // But less than 5 seconds
        
        // Verify the encoded password works
        assertThat(passwordEncoder.matches(testPassword, encodedPassword)).isTrue();
    }

    @Test
    public void testPasswordEncodingIsUnique() {
        // Test that same password produces different hashes (due to salt)
        String password = "SamePassword123!";
        
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);
        
        // Different hashes due to different salts
        assertThat(hash1).isNotEqualTo(hash2);
        
        // But both should match the original password
        assertThat(passwordEncoder.matches(password, hash1)).isTrue();
        assertThat(passwordEncoder.matches(password, hash2)).isTrue();
    }

    @Test
    public void testBCryptFormatValidation() {
        String password = "ValidationTest123!";
        String encoded = passwordEncoder.encode(password);
        
        // Validate BCrypt format: $2a$rounds$saltandpassworddata
        String[] parts = encoded.split("\\$");
        assertThat(parts).hasSize(4); // Should have 4 parts: ["", "2a", "12", "saltandpass"]
        assertThat(parts[1]).matches("2[aby]"); // Version identifier
        assertThat(parts[2]).isEqualTo("12"); // Strength/rounds should be 12
        assertThat(parts[3]).hasSize(53); // Salt (22) + Hash (31) = 53 chars
    }
}
