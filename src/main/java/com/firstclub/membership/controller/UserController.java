package com.firstclub.membership.controller;

import com.firstclub.membership.model.User;
import com.firstclub.membership.model.User.UserRole;
import com.firstclub.membership.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * REST Controller for comprehensive user management operations
 * 
 * This controller provides endpoints for:
 * - User registration and authentication
 * - Profile management
 * - Role-based operations
 * - Administrative functions
 * - User analytics and reporting
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Health Check and Info

    /**
     * Health check endpoint for user service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        UserService.UserStatistics stats = userService.getUserStatistics();
        
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "UserService",
            "timestamp", LocalDateTime.now(),
            "totalUsers", stats.getTotalUsers(),
            "activeUsers", stats.getActiveUsers(),
            "adminUsers", stats.getAdminUsers()
        ));
    }

    // User Registration and Authentication

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> request) {
        log.info("Registration request received for username: {}", request.get("username"));
        
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");

            // Basic validation
            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username, email, and password are required"
                ));
            }

            User user = userService.registerUser(username, email, password, firstName, lastName);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "User registered successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "roles", user.getRoles(),
                    "isActive", user.getIsActive(),
                    "createdAt", user.getCreatedAt()
                )
            ));
            
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Register a new admin user
     */
    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody Map<String, String> request) {
        log.info("Admin registration request received for username: {}", request.get("username"));
        
        try {
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");

            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username, email, and password are required"
                ));
            }

            User admin = userService.registerAdmin(username, email, password, firstName, lastName);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Admin registered successfully",
                "user", Map.of(
                    "id", admin.getId(),
                    "username", admin.getUsername(),
                    "email", admin.getEmail(),
                    "firstName", admin.getFirstName() != null ? admin.getFirstName() : "",
                    "lastName", admin.getLastName() != null ? admin.getLastName() : "",
                    "roles", admin.getRoles(),
                    "isActive", admin.getIsActive(),
                    "createdAt", admin.getCreatedAt()
                )
            ));
            
        } catch (RuntimeException e) {
            log.error("Admin registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Authenticate user login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody Map<String, String> request) {
        log.info("Login request received for: {}", request.get("usernameOrEmail"));
        
        try {
            String usernameOrEmail = request.get("usernameOrEmail");
            String password = request.get("password");

            if (usernameOrEmail == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Username/email and password are required"
                ));
            }

            Optional<User> userOpt = userService.authenticateUser(usernameOrEmail, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                        "lastName", user.getLastName() != null ? user.getLastName() : "",
                        "roles", user.getRoles(),
                        "isActive", user.getIsActive(),
                        "lastLogin", user.getLastLogin()
                    )
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid credentials or inactive account"
                ));
            }
            
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Login failed"
            ));
        }
    }

    // User Retrieval Operations

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        log.info("Getting user by ID: {}", id);
        
        Optional<User> userOpt = userService.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
            userMap.put("lastName", user.getLastName() != null ? user.getLastName() : "");
            userMap.put("phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            userMap.put("dateOfBirth", user.getDateOfBirth());
            userMap.put("roles", user.getRoles());
            userMap.put("isActive", user.getIsActive());
            userMap.put("emailVerified", user.getEmailVerified());
            userMap.put("createdAt", user.getCreatedAt());
            userMap.put("updatedAt", user.getUpdatedAt());
            userMap.put("lastLogin", user.getLastLogin());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "user", userMap
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all users with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting all users - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<User> users = userService.getAllActiveUsers(pageable);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "users", users.getContent().stream().map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "roles", user.getRoles(),
                    "isActive", user.getIsActive(),
                    "createdAt", user.getCreatedAt(),
                    "lastLogin", user.getLastLogin()
                )).toList(),
                "pagination", Map.of(
                    "page", users.getNumber(),
                    "size", users.getSize(),
                    "totalElements", users.getTotalElements(),
                    "totalPages", users.getTotalPages(),
                    "first", users.isFirst(),
                    "last", users.isLast()
                )
            ));
            
        } catch (Exception e) {
            log.error("Error getting users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving users"
            ));
        }
    }

    /**
     * Search users by query
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching users with query: {}", q);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<User> users = userService.searchUsers(q, pageable);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "query", q,
                "users", users.getContent().stream().map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "roles", user.getRoles(),
                    "isActive", user.getIsActive()
                )).toList(),
                "pagination", Map.of(
                    "page", users.getNumber(),
                    "size", users.getSize(),
                    "totalElements", users.getTotalElements(),
                    "totalPages", users.getTotalPages()
                )
            ));
            
        } catch (Exception e) {
            log.error("Error searching users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error searching users"
            ));
        }
    }

    // Role Management Operations

    /**
     * Add role to user
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<Map<String, Object>> addRoleToUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        log.info("Adding role to user ID: {}", id);
        
        try {
            String roleStr = request.get("role");
            if (roleStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Role is required"
                ));
            }

            UserRole role = UserRole.valueOf(roleStr.toUpperCase());
            User user = userService.addRoleToUser(id, role);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Role added successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "roles", user.getRoles()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid role: " + request.get("role")
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove role from user
     */
    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<Map<String, Object>> removeRoleFromUser(
            @PathVariable Long id,
            @PathVariable String role) {
        
        log.info("Removing role {} from user ID: {}", role, id);
        
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            User user = userService.removeRoleFromUser(id, userRole);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Role removed successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "roles", user.getRoles()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid role: " + role
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/by-role/{role}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String role) {
        log.info("Getting users with role: {}", role);
        
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "role", role,
                "count", users.size(),
                "users", users.stream().map(user -> Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "isActive", user.getIsActive(),
                    "createdAt", user.getCreatedAt()
                )).toList()
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid role: " + role
            ));
        }
    }

    /**
     * Get all admin users
     */
    @GetMapping("/admins")
    public ResponseEntity<Map<String, Object>> getAllAdmins() {
        log.info("Getting all admin users");
        
        List<User> admins = userService.getAllAdmins();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "count", admins.size(),
            "admins", admins.stream().map(admin -> Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "email", admin.getEmail(),
                "firstName", admin.getFirstName() != null ? admin.getFirstName() : "",
                "lastName", admin.getLastName() != null ? admin.getLastName() : "",
                "roles", admin.getRoles(),
                "isActive", admin.getIsActive(),
                "createdAt", admin.getCreatedAt(),
                "lastLogin", admin.getLastLogin()
            )).toList()
        ));
    }

    // Profile Management Operations

    /**
     * Update user profile
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        log.info("Updating profile for user ID: {}", id);
        
        try {
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            String phoneNumber = (String) request.get("phoneNumber");
            LocalDateTime dateOfBirth = request.get("dateOfBirth") != null ? 
                LocalDateTime.parse((String) request.get("dateOfBirth")) : null;

            User user = userService.updateUserProfile(id, firstName, lastName, phoneNumber, dateOfBirth);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                    "lastName", user.getLastName() != null ? user.getLastName() : "",
                    "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    "dateOfBirth", user.getDateOfBirth(),
                    "updatedAt", user.getUpdatedAt()
                )
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error updating profile"
            ));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        log.info("Password change request for user ID: {}", id);
        
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Old password and new password are required"
                ));
            }

            boolean success = userService.changePassword(id, oldPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password changed successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Incorrect old password"
                ));
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verify user email
     */
    @PutMapping("/{id}/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@PathVariable Long id) {
        log.info("Email verification request for user ID: {}", id);
        
        try {
            User user = userService.verifyEmail(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verified successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "emailVerified", user.getEmailVerified()
                )
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Administrative Operations

    /**
     * Toggle user account status
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Long id) {
        log.info("Toggling status for user ID: {}", id);
        
        try {
            User user = userService.toggleUserStatus(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User status updated successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "isActive", user.getIsActive()
                )
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        log.info("Delete request for user ID: {}", id);
        
        try {
            userService.deleteUser(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deleted successfully"
            ));
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Analytics and Reporting

    /**
     * Get user statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        log.info("Getting user statistics");
        
        UserService.UserStatistics stats = userService.getUserStatistics();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "statistics", Map.of(
                "totalUsers", stats.getTotalUsers(),
                "activeUsers", stats.getActiveUsers(),
                "inactiveUsers", stats.getInactiveUsers(),
                "adminUsers", stats.getAdminUsers(),
                "newUsersThisMonth", stats.getNewUsersThisMonth()
            )
        ));
    }

    /**
     * Get user analytics (alias for statistics)
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics() {
        return getUserStatistics();
    }

    /**
     * Get inactive users
     */
    @GetMapping("/inactive")
    public ResponseEntity<Map<String, Object>> getInactiveUsers(
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Getting users inactive for {} days", days);
        
        List<User> inactiveUsers = userService.getInactiveUsers(days);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "inactiveDays", days,
            "count", inactiveUsers.size(),
            "users", inactiveUsers.stream().map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                "lastName", user.getLastName() != null ? user.getLastName() : "",
                "lastLogin", user.getLastLogin(),
                "createdAt", user.getCreatedAt()
            )).toList()
        ));
    }

    // Utility Endpoints

    /**
     * Check username availability
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@PathVariable String username) {
        boolean available = userService.isUsernameAvailable(username);
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "available", available,
            "message", available ? "Username is available" : "Username is already taken"
        ));
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        boolean available = userService.isEmailAvailable(email);
        
        return ResponseEntity.ok(Map.of(
            "email", email,
            "available", available,
            "message", available ? "Email is available" : "Email is already registered"
        ));
    }
}
