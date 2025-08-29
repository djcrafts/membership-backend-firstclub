# High Level Design (HLD)
## FirstClub Membership Backend System

### System Overview
A Spring Boot-based membership management system that handles user subscriptions with tiered benefits and automatic progression based on user activity.

### System Architecture

#### 1. Application Layer Structure
```
FirstClub Membership Backend
├── Presentation Layer (Controllers)
├── Business Logic Layer (Services)
├── Data Access Layer (Repositories)
├── Domain Model Layer (Entities)
└── Configuration Layer (Security, Async)
```

#### 2. Core Components

##### 2.1 User Management Module
- **Purpose**: Handle user registration, authentication, and role management
- **Components**:
  - UserController: REST endpoints for user operations
  - UserService: Business logic for user management
  - UserRepository: Data access for user entities
  - User Entity: User domain model with roles

##### 2.2 Membership Management Module
- **Purpose**: Manage subscription plans, tiers, and user subscriptions
- **Components**:
  - MembershipController: REST endpoints for membership operations
  - MembershipService: Business logic for subscriptions and tier evaluation
  - MembershipPlanRepository: Data access for subscription plans
  - MembershipTierRepository: Data access for membership tiers
  - UserSubscriptionRepository: Data access for user subscriptions

##### 2.3 Activity Tracking Module
- **Purpose**: Track user activities for tier progression analysis
- **Components**:
  - Activity tracking within MembershipService
  - UserActivityRepository: Data access for user activities
  - UserActivity Entity: Activity domain model

#### 3. Data Model

##### 3.1 Core Entities
- **User**: User information with roles and authentication data
- **MembershipPlan**: Subscription plans (Monthly, Quarterly, Yearly)
- **MembershipTier**: Tier definitions (Silver, Gold, Platinum)
- **UserSubscription**: Active user subscriptions linking users to plans and tiers
- **UserActivity**: User activity records for tier evaluation

##### 3.2 Entity Relationships
```
User (1) ←→ (N) UserSubscription (N) ←→ (1) MembershipPlan
User (1) ←→ (N) UserActivity
UserSubscription (N) ←→ (1) MembershipTier
```

#### 4. Security Architecture

##### 4.1 Authentication & Authorization
- **Password Security**: BCrypt encoding with strength 12
- **Role-Based Access**: USER and ADMIN roles
- **Input Validation**: Comprehensive validation across all endpoints

##### 4.2 Data Security
- **SQL Injection Protection**: JPA parameterized queries
- **XSS Protection**: Spring Security defaults
- **Transaction Security**: ACID compliance with @Transactional

#### 5. API Architecture

##### 5.1 REST API Design
- **Base Path**: `/api/v1`
- **HTTP Methods**: GET, POST, PUT, DELETE
- **Response Format**: JSON with consistent structure
- **Error Handling**: Global exception handler with custom exceptions

##### 5.2 Endpoint Categories
- **User Management**: `/users/*` - Registration, authentication, role management
- **Membership Management**: `/memberships/*` - Plans, tiers, subscriptions, activities

#### 6. Concurrency & Performance

##### 6.1 Async Processing
- **Activity Recording**: Non-blocking activity tracking
- **Tier Evaluation**: Background tier progression evaluation
- **Thread Pools**: Dedicated thread pools for different operations

##### 6.2 Data Consistency
- **Transaction Management**: Proper isolation levels
- **Database Constraints**: Foreign keys and unique constraints
- **Optimistic Locking**: Conflict resolution for concurrent updates

#### 7. Deployment Architecture

##### 7.1 Development Environment
- **Database**: H2 in-memory
- **Port**: 8081
- **Context Path**: `/api/v1`

##### 7.2 Production Considerations
- **Database**: PostgreSQL/MySQL migration ready
- **Scaling**: Stateless design for horizontal scaling
- **Monitoring**: Health checks and metrics endpoints

### Integration Points

#### 8.1 External Dependencies
- **Spring Boot Framework**: Core application framework
- **Spring Data JPA**: Database abstraction
- **Spring Security**: Security framework
- **H2 Database**: Development database

#### 8.2 Configuration Management
- **Environment-specific**: Properties files for different environments
- **Security Configuration**: Centralized security setup
- **Thread Pool Configuration**: Async operation tuning

### Design Principles Applied

#### 9.1 SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extension through interfaces and inheritance
- **Dependency Inversion**: Dependency injection throughout

#### 9.2 Enterprise Patterns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer standardization
- **Builder Pattern**: Object construction consistency
