package com.firstclub.membership.repository;

import com.firstclub.membership.model.User;
import com.firstclub.membership.model.User.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * 
 * Provides comprehensive data access methods for user management,
 * authentication, and role-based queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic user queries
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);

    // Active user queries
    List<User> findByIsActiveTrue();
    
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    List<User> findByIsActiveFalse();

    // Role-based queries
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.isActive = true")
    List<User> findByRoleAndIsActiveTrue(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN :roles")
    List<User> findByRolesIn(@Param("roles") List<UserRole> roles);

    // Admin queries
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN ('ADMIN', 'SUPER_ADMIN')")
    List<User> findAllAdmins();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r IN ('ADMIN', 'SUPER_ADMIN') AND u.isActive = true")
    List<User> findActiveAdmins();

    // Search queries
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    // Email verification queries
    List<User> findByEmailVerifiedFalse();
    
    List<User> findByEmailVerifiedTrue();

    // Login tracking queries
    List<User> findByLastLoginAfter(LocalDateTime dateTime);
    
    List<User> findByLastLoginBefore(LocalDateTime dateTime);
    
    List<User> findByLastLoginIsNull();

    // Date range queries
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate")
    List<User> findUsersCreatedAfter(@Param("startDate") LocalDateTime startDate);

    // Statistics queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :date")
    long countUsersCreatedAfter(@Param("date") LocalDateTime date);

    // Custom update queries (handled in service layer)
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.lastLogin < :cutoffDate")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);
}
