package com.firstclub.membership.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.firstclub.membership.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for production-ready security features
 * Tests BCrypt password encoding, authentication, and authorization
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductionSecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1";
    }

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
    public void testUserRegistrationWithSecurePasswordEncoding() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uniqueUsername = "securitytest" + System.currentTimeMillis();
        String uniqueEmail = "security" + System.currentTimeMillis() + "@test.com";

        Map<String, Object> userRequest = Map.of(
            "username", uniqueUsername,
            "email", uniqueEmail,
            "firstName", "Security",
            "lastName", "Test",
            "password", "SecureTestPassword123!",
            "phoneNumber", "+1234567890",
            "dateOfBirth", "1990-01-01"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            getBaseUrl() + "/users/register", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) response.getBody().get("success")).isTrue();
        assertThat(response.getBody().get("message")).isEqualTo("User registered successfully");
        
        Map<String, Object> user = (Map<String, Object>) response.getBody().get("user");
        assertThat(user.get("username")).isEqualTo(uniqueUsername);
        assertThat(user.get("email")).isEqualTo(uniqueEmail);
    }

    @Test
    public void testAuthenticationWithBCryptVerification() {
        // First register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uniqueUsername = "authtest" + System.currentTimeMillis();
        String uniqueEmail = "auth" + System.currentTimeMillis() + "@test.com";

        Map<String, Object> userRequest = Map.of(
            "username", uniqueUsername,
            "email", uniqueEmail,
            "firstName", "Auth",
            "lastName", "Test",
            "password", "SecureTestPassword123!",
            "phoneNumber", "+1234567890",
            "dateOfBirth", "1990-01-01"
        );

        HttpEntity<Map<String, Object>> registerRequest = new HttpEntity<>(userRequest, headers);
        restTemplate.postForEntity(getBaseUrl() + "/users/register", registerRequest, Map.class);

        // Test successful login
        Map<String, String> loginRequest = Map.of(
            "usernameOrEmail", uniqueUsername,
            "password", "SecureTestPassword123!"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            getBaseUrl() + "/users/login", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) response.getBody().get("success")).isTrue();
        assertThat(response.getBody().get("message")).isEqualTo("Login successful");
    }

    @Test
    public void testInvalidPasswordRejection() {
        // First register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uniqueUsername = "invalidtest" + System.currentTimeMillis();
        String uniqueEmail = "invalid" + System.currentTimeMillis() + "@test.com";

        Map<String, Object> userRequest = Map.of(
            "username", uniqueUsername,
            "email", uniqueEmail,
            "firstName", "Invalid",
            "lastName", "Test",
            "password", "SecureTestPassword123!",
            "phoneNumber", "+1234567890",
            "dateOfBirth", "1990-01-01"
        );

        HttpEntity<Map<String, Object>> registerRequest = new HttpEntity<>(userRequest, headers);
        restTemplate.postForEntity(getBaseUrl() + "/users/register", registerRequest, Map.class);

        // Test with wrong password
        Map<String, String> loginRequest = Map.of(
            "usernameOrEmail", uniqueUsername,
            "password", "WrongPassword123!"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            getBaseUrl() + "/users/login", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) response.getBody().get("success")).isFalse();
        assertThat(response.getBody().get("message")).isEqualTo("Invalid credentials or inactive account");
    }

    @Test
    public void testPasswordChangeWithSecureEncoding() {
        // First register a user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uniqueUsername = "pwchange" + System.currentTimeMillis();
        String uniqueEmail = "pwchange" + System.currentTimeMillis() + "@test.com";

        Map<String, Object> userRequest = Map.of(
            "username", uniqueUsername,
            "email", uniqueEmail,
            "firstName", "Password",
            "lastName", "Change",
            "password", "SecureTestPassword123!",
            "phoneNumber", "+1234567890",
            "dateOfBirth", "1990-01-01"
        );

        HttpEntity<Map<String, Object>> registerRequest = new HttpEntity<>(userRequest, headers);
        ResponseEntity<Map> registerResponse = restTemplate.postForEntity(
            getBaseUrl() + "/users/register", registerRequest, Map.class);

        // Get user ID from registration response
        Map<String, Object> user = (Map<String, Object>) registerResponse.getBody().get("user");
        Integer userId = (Integer) user.get("id");

        // Change password
        Map<String, String> passwordChangeRequest = Map.of(
            "oldPassword", "SecureTestPassword123!",
            "newPassword", "NewSecurePassword456!"
        );

        HttpEntity<Map<String, String>> changeRequest = new HttpEntity<>(passwordChangeRequest, headers);

        ResponseEntity<Map> changeResponse = restTemplate.exchange(
            getBaseUrl() + "/users/" + userId + "/password",
            HttpMethod.PUT,
            changeRequest,
            Map.class
        );

        assertThat(changeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) changeResponse.getBody().get("success")).isTrue();
        assertThat(changeResponse.getBody().get("message")).isEqualTo("Password changed successfully");

        // Test login with new password
        Map<String, String> newLoginRequest = Map.of(
            "usernameOrEmail", uniqueUsername,
            "password", "NewSecurePassword456!"
        );

        HttpEntity<Map<String, String>> newLoginEntity = new HttpEntity<>(newLoginRequest, headers);
        ResponseEntity<Map> newLoginResponse = restTemplate.postForEntity(
            getBaseUrl() + "/users/login", newLoginEntity, Map.class);

        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) newLoginResponse.getBody().get("success")).isTrue();

        // Test that old password no longer works
        Map<String, String> oldLoginRequest = Map.of(
            "usernameOrEmail", uniqueUsername,
            "password", "SecureTestPassword123!"
        );

        HttpEntity<Map<String, String>> oldLoginEntity = new HttpEntity<>(oldLoginRequest, headers);
        ResponseEntity<Map> oldLoginResponse = restTemplate.postForEntity(
            getBaseUrl() + "/users/login", oldLoginEntity, Map.class);

        assertThat(oldLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((Boolean) oldLoginResponse.getBody().get("success")).isFalse();
    }

    @Test
    public void testMembershipEndpointsAreAccessible() {
        // Test membership health
        ResponseEntity<Map> healthResponse = restTemplate.getForEntity(
            getBaseUrl() + "/memberships/health", Map.class);

        assertThat(healthResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(healthResponse.getBody().get("status")).isEqualTo("OK");

        // Test membership plans
        ResponseEntity<Object[]> plansResponse = restTemplate.getForEntity(
            getBaseUrl() + "/memberships/plans", Object[].class);

        assertThat(plansResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(plansResponse.getBody()).isNotNull();
        assertThat(plansResponse.getBody().length).isGreaterThan(0);

        // Test membership tiers
        ResponseEntity<Object[]> tiersResponse = restTemplate.getForEntity(
            getBaseUrl() + "/memberships/tiers", Object[].class);

        assertThat(tiersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tiersResponse.getBody()).isNotNull();
        assertThat(tiersResponse.getBody().length).isGreaterThan(0);
    }
}
