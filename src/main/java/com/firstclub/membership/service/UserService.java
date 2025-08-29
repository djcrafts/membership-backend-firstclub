package com.firstclub.membership.service;

import com.firstclub.membership.model.User;
import com.firstclub.membership.model.User.UserRole;
import com.firstclub.membership.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for comprehensive user management operations
 * 
 * This service provides all user-related functionality including:
 * - User registration and authentication
 * - Role management and authorization
 * - Profile management
 * - Administrative operations
 * - User analytics and reporting
 */
@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // User Registration and Authentication

    /**
     * Registers a new user with default USER role
     */
    public User registerUser(String username, String email, String password, 
                           String firstName, String lastName) {
        log.info("Attempting to register new user: {}", username);
        
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // Create new user
        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .firstName(firstName)
            .lastName(lastName)
            .addRole(UserRole.USER)
            .build();

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user: {} with ID: {}", username, savedUser.getId());
        
        return savedUser;
    }

    /**
     * Registers a new admin user with ADMIN role
     */
    public User registerAdmin(String username, String email, String password, 
                            String firstName, String lastName) {
        log.info("Attempting to register new admin: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        User admin = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .firstName(firstName)
            .lastName(lastName)
            .addRole(UserRole.ADMIN)
            .build();
        
        admin.setEmailVerified(true); // Admins are pre-verified

        User savedAdmin = userRepository.save(admin);
        log.info("Successfully registered admin: {} with ID: {}", username, savedAdmin.getId());
        
        return savedAdmin;
    }

    /**
     * Authenticates user by username/email and password
     */
    public Optional<User> authenticateUser(String usernameOrEmail, String password) {
        log.info("Attempting to authenticate user: {}", usernameOrEmail);
        
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check password using BCrypt encoder
            if (passwordEncoder.matches(password, user.getPassword()) && user.getIsActive()) {
                user.updateLastLogin();
                userRepository.save(user);
                log.info("Successfully authenticated user: {}", user.getUsername());
                return Optional.of(user);
            }
        }
        
        log.warn("Authentication failed for user: {}", usernameOrEmail);
        return Optional.empty();
    }

    // User Retrieval Operations

    /**
     * Finds user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Finds user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Gets all active users with pagination
     */
    public Page<User> getAllActiveUsers(Pageable pageable) {
        log.info("Retrieving active users with pagination");
        return userRepository.findByIsActiveTrue(pageable);
    }

    /**
     * Searches users by query string
     */
    public Page<User> searchUsers(String query, Pageable pageable) {
        log.info("Searching users with query: {}", query);
        return userRepository.searchUsers(query, pageable);
    }

    // Role Management Operations

    /**
     * Adds role to user
     */
    public User addRoleToUser(Long userId, UserRole role) {
        log.info("Adding role {} to user ID: {}", role, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.addRole(role);
        User updatedUser = userRepository.save(user);
        
        log.info("Successfully added role {} to user: {}", role, user.getUsername());
        return updatedUser;
    }

    /**
     * Removes role from user
     */
    public User removeRoleFromUser(Long userId, UserRole role) {
        log.info("Removing role {} from user ID: {}", role, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.removeRole(role);
        User updatedUser = userRepository.save(user);
        
        log.info("Successfully removed role {} from user: {}", role, user.getUsername());
        return updatedUser;
    }

    /**
     * Updates user roles completely
     */
    public User updateUserRoles(Long userId, Set<UserRole> newRoles) {
        log.info("Updating roles for user ID: {} to: {}", userId, newRoles);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setRoles(newRoles);
        User updatedUser = userRepository.save(user);
        
        log.info("Successfully updated roles for user: {}", user.getUsername());
        return updatedUser;
    }

    /**
     * Gets all users with specific role
     */
    public List<User> getUsersByRole(UserRole role) {
        log.info("Retrieving users with role: {}", role);
        return userRepository.findByRole(role);
    }

    /**
     * Gets all admin users
     */
    public List<User> getAllAdmins() {
        log.info("Retrieving all admin users");
        return userRepository.findAllAdmins();
    }

    // User Profile Management

    /**
     * Updates user profile information
     */
    public User updateUserProfile(Long userId, String firstName, String lastName, 
                                String phoneNumber, LocalDateTime dateOfBirth) {
        log.info("Updating profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setDateOfBirth(dateOfBirth);
        
        User updatedUser = userRepository.save(user);
        log.info("Successfully updated profile for user: {}", user.getUsername());
        
        return updatedUser;
    }

    /**
     * Changes user password
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Attempting to change password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Verify old password using BCrypt encoder
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Password change failed - incorrect old password for user: {}", user.getUsername());
            return false;
        }
        
        // Update password with new encoded password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Successfully changed password for user: {}", user.getUsername());
        return true;
    }

    /**
     * Verifies user email
     */
    public User verifyEmail(Long userId) {
        log.info("Verifying email for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setEmailVerified(true);
        User updatedUser = userRepository.save(user);
        
        log.info("Successfully verified email for user: {}", user.getUsername());
        return updatedUser;
    }

    // Administrative Operations

    /**
     * Activates/deactivates user account
     */
    public User toggleUserStatus(Long userId) {
        log.info("Toggling status for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setIsActive(!user.getIsActive());
        User updatedUser = userRepository.save(user);
        
        log.info("User {} status changed to: {}", user.getUsername(), user.getIsActive() ? "ACTIVE" : "INACTIVE");
        return updatedUser;
    }

    /**
     * Permanently deletes user account
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        userRepository.delete(user);
        log.info("Successfully deleted user: {}", user.getUsername());
    }

    // Analytics and Reporting

    /**
     * Gets user statistics
     */
    public UserStatistics getUserStatistics() {
        log.info("Generating user statistics");
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long adminUsers = userRepository.countByRole(UserRole.ADMIN);
        long newUsersThisMonth = userRepository.countUsersCreatedAfter(
            LocalDateTime.now().minusMonths(1));
        
        return new UserStatistics(totalUsers, activeUsers, adminUsers, newUsersThisMonth);
    }

    /**
     * Gets inactive users (no login in specified days)
     */
    public List<User> getInactiveUsers(int days) {
        log.info("Finding users inactive for {} days", days);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userRepository.findInactiveUsers(cutoffDate);
    }

    // Helper Methods

    /**
     * Checks if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Checks if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Checks if user has specific role
     */
    public boolean userHasRole(Long userId, UserRole role) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> user.hasRole(role)).orElse(false);
    }

    /**
     * Inner class for user statistics
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long adminUsers;
        private final long newUsersThisMonth;

        public UserStatistics(long totalUsers, long activeUsers, long adminUsers, long newUsersThisMonth) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.adminUsers = adminUsers;
            this.newUsersThisMonth = newUsersThisMonth;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getAdminUsers() { return adminUsers; }
        public long getNewUsersThisMonth() { return newUsersThisMonth; }
        public long getInactiveUsers() { return totalUsers - activeUsers; }
    }
}
