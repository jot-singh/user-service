# User Service Requirements for Ecommerce Platform

## Project Overview
Complete the User Service to provide authentication, authorization, and user management for an ecommerce platform where the Product Service will consume user authentication services.

## Current State Analysis
- ✅ Basic Spring Boot setup with Spring Security
- ✅ JWT token generation and validation
- ✅ Basic OAuth2 Authorization Server configuration
- ✅ **COMPLETED: Proper BCrypt password encryption**
- ✅ **COMPLETED: Enhanced User entity with ecommerce-specific fields**
- ✅ **COMPLETED: Ecommerce role system (CUSTOMER, MERCHANT, ADMIN, MODERATOR)**
- ✅ **COMPLETED: Account security features (locking, email verification)**
- ✅ **COMPLETED: Input validation with Bean Validation**
- ❌ Missing OAuth2 client configuration for Product Service
- ❌ Missing comprehensive user management APIs
- ❌ Missing role-based access control implementation
- ❌ Missing proper error handling
- ❌ Missing API documentation

## Progress Status

### ✅ COMPLETED - Phase 1: Foundation & Security (2/5 commits done)

#### ✅ Commit 1: Fix Password Security - COMPLETED ✅
- [x] Implement proper BCrypt password encoding
- [x] Fix password validation in login
- [x] Add password strength validation (8+ chars, uppercase, lowercase, number)
- [x] Remove hardcoded passwords from UserDetailsService
- [x] Add comprehensive password handling in signup/update methods
- **Files**: `SubjectAuthServiceImpl.java`, `SpringSecurityConfig.java`
- **Commit Hash**: `04e9a32`

#### ✅ Commit 2: Enhance User Entity & Role System - COMPLETED ✅
- [x] Add ecommerce-specific roles (CUSTOMER, MERCHANT, ADMIN, MODERATOR)
- [x] Add user profile fields (firstName, lastName, phone, createdAt, etc.)
- [x] Add account security features (emailVerified, accountLocked, failedLoginAttempts)
- [x] Add proper JPA annotations, constraints, and indexes
- [x] Add validation annotations for input validation
- [x] Implement business logic methods (getFullName, isAccountActive, etc.)
- [x] Create database migration script
- [x] Add spring-boot-starter-validation dependency
- **Files**: `User.java`, `Role.java`, `pom.xml`, `V002_enhance_user_table.sql`
- **Commit Hash**: `fbd5117`

### 🚧 IN PROGRESS - Phase 1: Foundation & Security (3/5 commits remaining)

#### ⏭️ NEXT: Commit 3: OAuth2 Client Configuration
- [ ] Configure OAuth2 client for Product Service
- [ ] Add client registration in database
- [ ] Create client management endpoints
- [ ] Set up proper OAuth2 flows for service-to-service communication
- **Files**: `SpringSecurityConfig.java`, `data.sql`, `ClientController.java`

#### ⏭️ Commit 4: Comprehensive Error Handling
- [ ] Create custom exception classes
- [ ] Add global exception handler
- [ ] Standardize API response format
- [ ] Add proper HTTP status codes
- **Files**: `GlobalExceptionHandler.java`, `ApiResponse.java`, `ErrorCodes.java`

#### ⏭️ Commit 5: Input Validation & DTOs Enhancement
- [ ] Enhance existing DTOs with proper validation
- [ ] Create comprehensive DTOs for new user fields
- [ ] Add request/response validation
- [ ] Add custom validators for business rules
- **Files**: `AuthRequestDto.java`, `UserProfileDto.java`, validation classes

### 📋 PENDING - Phase 2: User Management Enhancement (4 commits)

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

#### Commit 9: Session Management Enhancement
- [ ] Enhance session tracking
- [ ] Add session expiry handling
- [ ] Add concurrent session control
- **Files**: `SessionService.java`, `SessionController.java`

### 📋 PENDING - Phase 3: Integration & APIs (3 commits)

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

### 📋 PENDING - Phase 4: Advanced Features (3 commits)

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

### Database Schema Status
```sql
-- ✅ IMPLEMENTED: Enhanced User table
users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'MERCHANT', 'ADMIN', 'MODERATOR') DEFAULT 'CUSTOMER',
    email_verified BOOLEAN DEFAULT FALSE,
    account_locked BOOLEAN DEFAULT FALSE,
    failed_login_attempts INT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    last_login TIMESTAMP
);

-- ❌ TODO: User addresses
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

### API Endpoints Status
```
✅ IMPLEMENTED Authentication:
POST /auth/login       - Enhanced with account locking
POST /auth/signup      - Enhanced with password validation  
POST /auth/logout      - Basic implementation
GET  /auth/validate    - Basic implementation
PUT  /auth/update/{id} - Enhanced with proper validation

❌ TODO User Management:
GET    /users/profile
PUT    /users/profile
POST   /users/addresses
GET    /users/addresses
PUT    /users/addresses/{id}
DELETE /users/addresses/{id}

❌ TODO Internal APIs (for Product Service):
GET /internal/users/{userId}
GET /internal/users/{userId}/permissions
POST /internal/users/{userId}/activity

❌ TODO Admin APIs:
GET /admin/users
POST /admin/users/{userId}/lock
POST /admin/users/{userId}/unlock
```

### Security Features Status
- ✅ **BCrypt password hashing**
- ✅ **Password strength validation**
- ✅ **Account locking after failed attempts**
- ✅ **Email verification requirement**
- ✅ **Role-based user types**
- ❌ OAuth2 client configuration for Product Service
- ❌ Method-level security
- ❌ Rate limiting
- ❌ CORS configuration

## Recent Changes (Last Session)

### Commit 1 (04e9a32): Password Security Implementation
- Fixed insecure plain text password comparison
- Added BCryptPasswordEncoder injection
- Implemented password strength validation regex
- Enhanced login/signup methods with proper encoding
- Removed hardcoded passwords from Spring Security config

### Commit 2 (fbd5117): User Entity & Role Enhancement  
- Added 4 ecommerce-specific roles with business logic methods
- Enhanced User entity with 8 new fields for ecommerce use cases
- Added account security features (locking, email verification, failed attempts)
- Implemented audit fields (created_at, updated_at, last_login)
- Added comprehensive validation annotations
- Created database migration script
- Enhanced login logic with security checks

## Integration Requirements
1. **Product Service Integration**: JWT token validation, user context APIs
2. **Database**: MySQL with proper indexing and constraints ✅ 
3. **Caching**: Redis for session management (future)
4. **Message Queue**: Kafka for user events (future)
5. **Monitoring**: Actuator endpoints, custom metrics

## Success Criteria
1. ✅ **Secure password handling and user authentication**
2. ✅ **Enhanced user entity supporting ecommerce use cases**
3. ❌ Product Service can authenticate users via JWT tokens
4. ❌ Comprehensive user management with proper security
5. ❌ Role-based access control working
6. ❌ All APIs properly documented
7. ❌ Proper error handling and validation
8. ❌ Event-driven architecture for user actions

## Getting Started for Next Session
1. **Continue with Commit 3**: OAuth2 Client Configuration for Product Service integration
2. **Focus areas**: Client registration, JWT configuration, service-to-service auth
3. **Testing**: Ensure Product Service can validate tokens from User Service
4. **Database**: May need client registration tables

## Notes for Development
- Small, focused commits work well for maintainability
- Each commit should be fully functional and tested
- Database migrations should be backward compatible where possible
- Keep security as the top priority throughout development

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
