# Low Level Design (LLD)
## FirstClub Membership Backend System

### Package Structure
```
com.firstclub.membership
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── UserController.java
│   └── MembershipController.java
├── service/
│   ├── UserService.java
│   └── MembershipService.java
├── repository/
│   ├── UserRepository.java
│   ├── MembershipPlanRepository.java
│   ├── MembershipTierRepository.java
│   ├── UserSubscriptionRepository.java
│   └── UserActivityRepository.java
├── model/
│   ├── User.java
│   ├── MembershipPlan.java
│   ├── MembershipTier.java
│   ├── UserSubscription.java
│   └── UserActivity.java
├── exception/
│   ├── MembershipException.java
│   ├── UserNotFoundException.java
│   ├── SubscriptionException.java
│   └── GlobalExceptionHandler.java
├── util/
│   └── Constants.java
└── MembershipBackendApplication.java
```

### Class Design Details

#### 1. Controller Layer

##### 1.1 UserController
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    // Endpoints:
    // POST /register - User registration
    // POST /login - User authentication  
    // POST /register-admin - Admin registration
    // POST /{userId}/roles/{role} - Add role
    // DELETE /{userId}/roles/{role} - Remove role
    // GET /analytics - User statistics
    // GET / - Get all users (paginated)
}
```

##### 1.2 MembershipController
```java
@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController {
    // Endpoints:
    // GET /plans - Get membership plans
    // GET /tiers - Get membership tiers
    // POST /subscribe - Subscribe user to plan/tier
    // GET /subscription/{userId} - Get user subscription
    // POST /activity - Record user activity
}
```

#### 2. Service Layer

##### 2.1 UserService
```java
@Service
@Transactional
public class UserService {
    // Methods:
    // registerUser() - Create new user with USER role
    // registerAdmin() - Create admin user with ADMIN role
    // authenticateUser() - Validate credentials with BCrypt
    // addRoleToUser() - Assign role to user
    // removeRoleFromUser() - Remove role from user
    // getUserStatistics() - Generate analytics
    // changePassword() - Update password with BCrypt
}
```

##### 2.2 MembershipService
```java
@Service
@Transactional
public class MembershipService {
    // Methods:
    // getAllPlans() - Retrieve active plans
    // getAllTiers() - Retrieve active tiers
    // subscribe() - Create user subscription
    // getCurrentSubscription() - Get active subscription
    // recordActivity() - Async activity recording
    // evaluateAndUpgradeTier() - Async tier evaluation
    // isUserEligibleForTier() - Check tier eligibility
}
```

#### 3. Repository Layer

##### 3.1 UserRepository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom queries:
    // findByUsername() - Find user by username
    // findByEmail() - Find user by email
    // findByUsernameOrEmail() - Find by username or email
    // existsByUsername() - Check username exists
    // existsByEmail() - Check email exists
    // findByRole() - Find users by role
    // countActiveUsers() - Count active users
}
```

##### 3.2 MembershipPlanRepository
```java
@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    // Custom queries:
    // findAllActivePlansOrderedByDuration() - Active plans by duration
    // findByPlanType() - Find by plan type
}
```

##### 3.3 MembershipTierRepository
```java
@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    // Custom queries:
    // findAllActiveTiersOrderedByLevel() - Active tiers by level
    // findQualifyingTiers() - Tiers user qualifies for
    // findTiersAccessibleByCohort() - Tiers by cohort access
}
```

#### 4. Model Layer

##### 4.1 User Entity
```java
@Entity
@Table(name = "users")
public class User {
    // Fields:
    // Long id - Primary key
    // String username - Unique username
    // String email - Unique email
    // String password - BCrypt encoded password
    // String firstName, lastName - User names
    // String phoneNumber - Contact number
    // LocalDateTime dateOfBirth - Birth date
    // Boolean isActive - Account status
    // Boolean emailVerified - Email verification status
    // LocalDateTime createdAt, updatedAt - Timestamps
    // LocalDateTime lastLogin - Last login time
    // Set<UserRole> roles - User roles (USER, ADMIN)
}
```

##### 4.2 MembershipPlan Entity
```java
@Entity
@Table(name = "membership_plans")
public class MembershipPlan {
    // Fields:
    // Long id - Primary key
    // String name - Plan name
    // PlanType planType - MONTHLY, QUARTERLY, YEARLY
    // BigDecimal price - Plan price
    // Integer durationMonths - Plan duration
    // String description - Plan description
    // Boolean isActive - Plan status
    // LocalDateTime createdAt, updatedAt - Timestamps
}
```

##### 4.3 MembershipTier Entity
```java
@Entity
@Table(name = "membership_tiers")
public class MembershipTier {
    // Fields:
    // Long id - Primary key
    // String name - Tier name
    // TierLevel level - SILVER, GOLD, PLATINUM
    // BigDecimal discountPercentage - Discount rate
    // Boolean freeDelivery - Delivery benefit
    // Integer minOrdersRequired - Order requirement
    // BigDecimal minOrderValueMonthly - Spending requirement
    // String description - Tier description
    // Boolean isActive - Tier status
    // Set<String> eligibleCohorts - Cohort restrictions
}
```

##### 4.4 UserSubscription Entity
```java
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {
    // Fields:
    // Long id - Primary key
    // String userId - Foreign key to user
    // MembershipPlan membershipPlan - Plan reference
    // MembershipTier membershipTier - Tier reference
    // SubscriptionStatus status - ACTIVE, CANCELLED, EXPIRED
    // LocalDateTime startDate, endDate - Subscription period
    // Boolean autoRenew - Auto-renewal setting
    // LocalDateTime cancelledAt - Cancellation time
    // String cancellationReason - Cancellation reason
    // LocalDateTime createdAt, updatedAt - Timestamps
}
```

##### 4.5 UserActivity Entity
```java
@Entity
@Table(name = "user_activities")
public class UserActivity {
    // Fields:
    // Long id - Primary key
    // String userId - Foreign key to user
    // ActivityType activityType - ORDER_PLACED, PURCHASE, LOGIN, REVIEW_POSTED
    // Integer orderCount - Number of orders
    // BigDecimal orderValue - Order value
    // String cohort - User cohort
    // LocalDateTime activityDate - Activity timestamp
    // String monthYear - Month-year for aggregation
}
```

#### 5. Exception Handling

##### 5.1 Custom Exceptions
```java
// MembershipException - Base exception
// UserNotFoundException - User not found
// SubscriptionException - Subscription errors
//   ├── ActiveSubscriptionExistsException
//   ├── NoActiveSubscriptionException
//   └── TierEligibilityException
```

##### 5.2 GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Methods:
    // handleUserNotFoundException() - 404 response
    // handleSubscriptionException() - 400 response
    // handleMembershipException() - 400 response
    // handleIllegalArgumentException() - 400 response
    // handleRuntimeException() - 500 response
}
```

#### 6. Configuration Classes

##### 6.1 SecurityConfig
```java
@Configuration
public class SecurityConfig {
    // Beans:
    // passwordEncoder() - BCryptPasswordEncoder with strength 12
}
```

#### 7. Application Entry Point

##### 7.1 MembershipBackendApplication
```java
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class MembershipBackendApplication {
    // Main method with Spring Boot startup
}
```

### Data Flow Patterns

#### 8.1 User Registration Flow
1. UserController receives registration request
2. UserService validates input and checks duplicates
3. Password encoded with BCrypt (strength 12)
4. User entity created with default USER role
5. UserRepository persists to database
6. Response returned with user details

#### 8.2 Subscription Flow
1. MembershipController receives subscription request
2. MembershipService validates user, plan, and tier
3. Eligibility check performed for tier requirements
4. UserSubscription entity created with ACTIVE status
5. UserSubscriptionRepository persists subscription
6. Response returned with subscription details

#### 8.3 Activity Tracking Flow
1. MembershipController receives activity request
2. MembershipService processes activity asynchronously
3. UserActivity entity created with activity details
4. UserActivityRepository persists activity
5. Background tier evaluation triggered
6. Automatic tier upgrade if qualified

### Database Schema Design

#### 9.1 Table Relationships
```sql
users (1) ←→ (N) user_subscriptions (N) ←→ (1) membership_plans
users (1) ←→ (N) user_activities
user_subscriptions (N) ←→ (1) membership_tiers
users (1) ←→ (N) user_roles
```

#### 9.2 Key Constraints
- Primary keys: Auto-generated IDs
- Foreign keys: Referential integrity maintained
- Unique constraints: Username, email uniqueness
- Not null constraints: Essential fields required
- Check constraints: Enum value validation

### Transaction Management

#### 10.1 Service Layer Transactions
- @Transactional on service classes
- Read-only transactions for query operations
- Propagation settings for nested calls
- Isolation levels for data consistency

#### 10.2 Async Operations
- @Async for non-blocking operations
- Separate thread pools for different operations
- CompletableFuture for async method returns
- Exception handling in async contexts
