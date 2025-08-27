# User Service Requirements for Ecommerce Platform

## Project Overview
Complete the User Service to provide authentication, authorization, and user management for an ecommerce platform where the Product Service will consume user authentication services.

## Current State Analysis
- ✅ Basic Spring Boot setup with Spring Security
- ✅ JWT token generation and validation
- ✅ Basic OAuth2 Authorization Server configuration
- ✅ Simple user authentication (login/signup/logout)
- ✅ MySQL database integration
- ❌ Missing proper password encryption
- ❌ Missing comprehensive user management
- ❌ Missing OAuth2 client configuration for Product Service
- ❌ Missing role-based access control
- ❌ Missing proper error handling
- ❌ Missing API documentation

## Requirements Breakdown

### Phase 1: Foundation & Security (Critical)
**Target: 5 small commits**

#### Commit 1: Fix Password Security
- [ ] Implement proper BCrypt password encoding
- [ ] Fix password validation in login
- [ ] Add password strength validation
- **Files**: `SubjectAuthServiceImpl.java`, `SpringSecurityConfig.java`

#### Commit 2: Enhance User Entity & Role System
- [ ] Add ecommerce-specific roles (CUSTOMER, MERCHANT, ADMIN)
- [ ] Add user profile fields (firstName, lastName, phone, createdAt, etc.)
- [ ] Add proper JPA annotations and constraints
- **Files**: `User.java`, `Role.java`

#### Commit 3: OAuth2 Client Configuration
- [ ] Configure OAuth2 client for Product Service
- [ ] Add client registration in database
- [ ] Create client management endpoints
- **Files**: `SpringSecurityConfig.java`, `data.sql`, `ClientController.java`

#### Commit 4: Comprehensive Error Handling
- [ ] Create custom exception classes
- [ ] Add global exception handler
- [ ] Standardize API response format
- **Files**: `GlobalExceptionHandler.java`, `ApiResponse.java`, `ErrorCodes.java`

#### Commit 5: Input Validation & DTOs
- [ ] Add proper validation annotations
- [ ] Create comprehensive DTOs
- [ ] Add request/response validation
- **Files**: `AuthRequestDto.java`, `UserProfileDto.java`, validation classes

### Phase 2: User Management Enhancement (High Priority)
**Target: 4 small commits**

#### Commit 6: User Profile Management
- [ ] Add user profile CRUD operations
- [ ] Add address management
- [ ] Add user preferences
- **Files**: `UserController.java`, `UserService.java`, `Address.java`

#### Commit 7: Role-Based Access Control
- [ ] Implement method-level security
- [ ] Add permission-based access
- [ ] Create admin endpoints
- **Files**: `SecurityConfig.java`, `AdminController.java`, `@PreAuthorize` annotations

#### Commit 8: User Registration Enhancement
- [ ] Add email verification
- [ ] Add account activation
- [ ] Add welcome email functionality
- **Files**: `RegistrationService.java`, `EmailService.java`, `VerificationToken.java`

#### Commit 9: Session Management
- [ ] Enhance session tracking
- [ ] Add session expiry handling
- [ ] Add concurrent session control
- **Files**: `SessionService.java`, `SessionController.java`

### Phase 3: Integration & APIs (Medium Priority)
**Target: 3 small commits**

#### Commit 10: Service-to-Service Authentication
- [ ] Create internal API endpoints for Product Service
- [ ] Add service account authentication
- [ ] Add user context APIs
- **Files**: `InternalUserController.java`, `ServiceAccountConfig.java`

#### Commit 11: Event System
- [ ] Add user event publishing
- [ ] Create event entities
- [ ] Add Kafka integration for user events
- **Files**: `UserEventPublisher.java`, `UserEvent.java`, `KafkaConfig.java`

#### Commit 12: API Documentation
- [ ] Add OpenAPI/Swagger configuration
- [ ] Document all endpoints
- [ ] Add API versioning
- **Files**: `OpenApiConfig.java`, `@Operation` annotations

### Phase 4: Advanced Features (Low Priority)
**Target: 3 small commits**

#### Commit 13: Multi-Factor Authentication
- [ ] Add TOTP support
- [ ] Add SMS/Email OTP
- [ ] Add backup codes
- **Files**: `MfaService.java`, `TotpController.java`

#### Commit 14: Social Authentication
- [ ] Add Google OAuth2 integration
- [ ] Add social profile linking
- [ ] Add social login endpoints
- **Files**: `SocialAuthController.java`, `GoogleOAuth2Config.java`

#### Commit 15: Analytics & Monitoring
- [ ] Add user activity tracking
- [ ] Add login analytics
- [ ] Add health checks and metrics
- **Files**: `UserAnalyticsService.java`, `MetricsConfig.java`

## Technical Specifications

### Database Schema Requirements
```sql
-- Enhanced User table
users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'MERCHANT', 'ADMIN'),
    email_verified BOOLEAN DEFAULT FALSE,
    account_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- User addresses
user_addresses (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    is_default BOOLEAN DEFAULT FALSE
);
```

### API Endpoints Design
```
Authentication:
POST /auth/login
POST /auth/signup
POST /auth/logout
GET  /auth/validate
POST /auth/refresh

User Management:
GET    /users/profile
PUT    /users/profile
POST   /users/addresses
GET    /users/addresses
PUT    /users/addresses/{id}
DELETE /users/addresses/{id}

Internal APIs (for Product Service):
GET /internal/users/{userId}
GET /internal/users/{userId}/permissions
POST /internal/users/{userId}/activity

Admin APIs:
GET /admin/users
POST /admin/users/{userId}/lock
POST /admin/users/{userId}/unlock
```

### Integration Requirements
1. **Product Service Integration**: JWT token validation, user context APIs
2. **Database**: MySQL with proper indexing and constraints
3. **Caching**: Redis for session management (future)
4. **Message Queue**: Kafka for user events (future)
5. **Monitoring**: Actuator endpoints, custom metrics

## Success Criteria
1. ✅ Product Service can authenticate users via JWT tokens
2. ✅ Comprehensive user management with proper security
3. ✅ Role-based access control working
4. ✅ All APIs properly documented
5. ✅ Proper error handling and validation
6. ✅ Database schema optimized for ecommerce use cases
7. ✅ Event-driven architecture for user actions

## Getting Started
1. Start with Phase 1, Commit 1 (Password Security)
2. Each commit should be small and focused
3. Run tests after each commit
4. Document changes in commit messages
5. Ensure backward compatibility where possible

Let's begin with Commit 1: Fix Password Security!
