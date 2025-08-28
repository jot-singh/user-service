# User Service Requirements for Ecommerce Platform

## Project Overview
Complete the User Service to provide authentication, authorization, and user management for an ecommerce platform where the Product Service will consume user authentication services.

## 🎯 Current Project Status

### ✅ COMPLETED COMMITS
- **Commit 1-5**: ✅ COMPLETED (OAuth2, error handling, validation system)
- **Commit 6**: ✅ COMPLETED (User Profile Management)
- **Commit 7**: ✅ COMPLETED (Role-Based Access Control - RBAC)
- **Commit 8**: ✅ COMPLETED (User Registration Enhancement)

- **Commit 9**: ✅ COMPLETED (Session Management)

### 🎯 NEXT TARGET: Commit 10
**Service-to-Service Authentication**
- Create internal API endpoints for Product Service
- Add service account authentication
- Add user context APIs

### 📊 OVERALL PROGRESS
- **Phase 1 (Foundation & Security)**: 5/5 commits ✅ COMPLETED
- **Phase 2 (User Management Enhancement)**: 4/4 commits ✅ COMPLETED
- **Phase 3 (Integration & APIs)**: 0/3 commits ⏳ PENDING
- **Phase 4 (Advanced Features)**: 0/3 commits ⏳ PENDING

**Total Progress: 9/15 commits completed (60%)**

## Current State Analysis
- ✅ Basic Spring Boot setup with Spring Security
- ✅ JWT token generation and validation
- ✅ **COMPLETED: OAuth2 Authorization Server configuration**
- ✅ Simple user authentication (login/signup/logout)
- ✅ **COMPLETED: H2 and MySQL database integration with profiles**
- ✅ Implemented proper password encryption with BCrypt
- ✅ **COMPLETED: Comprehensive user management with profile and address CRUD**
- ✅ **COMPLETED: OAuth2 client configuration for Product Service**
- ✅ **COMPLETED: Role-based access control (RBAC) with method-level security**
- ✅ **COMPLETED: Comprehensive error handling**
- ✅ **COMPLETED: Input validation & DTOs system**
- ❌ Missing API documentation

## 🎉 Recently Completed Features

### OAuth2 Authorization Server (Commit 3) ✅
**Complete service-to-service authentication implementation:**
- **OAuth2 Authorization Server** with JWT token generation using RSA256 signatures
- **Client Credentials Flow** for secure service-to-service authentication  
- **Token Introspection & Validation** endpoints for token verification
- **JWK Set Endpoint** for JWT signature verification
- **Client Registration Management** with REST APIs and bootstrap helpers
- **Profile-based Database Configuration** (H2 for local, MySQL for dev)
- **Product Service Integration** ready with pre-configured client
- **Comprehensive OAuth2 Schema** with all required tables and indexes

**Key Endpoints Working:**
- `GET /.well-known/oauth-authorization-server` - Authorization server metadata
- `POST /oauth2/token` - Token generation (client credentials flow)
- `POST /oauth2/introspect` - Token validation and introspection  
- `GET /oauth2/jwks` - JWT signature verification keys
- `POST /clients/bootstrap/product-service` - Easy client setup

**Security Features:**
- RSA256 JWT token signatures with automatic key generation
- BCrypt password encoding for OAuth2 client secrets
- Scope-based access control (`user.read`, `user.validate`)
- Secure client authentication methods (basic, post)

The Product Service can now authenticate using client credentials and validate user tokens through the authorization server.

### Input Validation & DTOs System (Commit 5) ✅
**Comprehensive validation framework for secure user operations:**
- **Enhanced DTOs** with Jakarta Bean Validation annotations
- **Custom Validation Annotations** (@StrongPassword, @ValidUsername)
- **Validation Utilities** for business rule validation
- **Enhanced Controllers** with @Valid annotations for automatic validation
- **Comprehensive Testing** with 16 test cases covering all scenarios
- **Complete Documentation** with usage examples and best practices

**Key Features:**
- Strong password validation with configurable requirements
- Username format validation with reserved name protection
- Email, phone, and URL format validation
- Business rule validation (admin username restrictions, role validation)
- Consistent error handling and validation messages
- Performance-optimized validation with compiled regex patterns

**Enhanced DTOs:**
- Request DTOs: AuthRequestDto, LogoutRequestDto, UserUpdateRequestDto, PasswordChangeRequestDto, UserSearchRequestDto
- Response DTOs: UserResponseDto, PaginatedResponseDto, enhanced BaseResponseDto and AuthResponseDto
- All DTOs include comprehensive validation and security constraints

The validation system provides a robust foundation for secure, validated user operations and is ready for the upcoming user management features.

### User Profile Management (Commit 6) ✅
**Complete user profile and address management system:**
- **Enhanced User Entity** with comprehensive profile fields and business logic
- **User Preferences** (language, currency, timezone, notifications)
- **Full CRUD Operations** for user profiles with validation
- **Advanced Address Management** with multiple addresses per user
- **Smart Address Features** (full address, short address, default management)
- **Comprehensive Testing** with full test suite for all operations

**Key Features:**
- User profile CRUD with validation and business rules
- Address management (Home, Work, Shipping, etc.) with default address logic
- User preferences and settings management
- Proper indexing and foreign key constraints in database schema
- Full test coverage for all profile and address operations

### Role-Based Access Control (Commit 7) ✅
**Comprehensive security system with fine-grained access control:**
- **Method-Level Security** with `@PreAuthorize` annotations throughout the application
- **Custom Permission Evaluator** for fine-grained access control
- **Admin Controller** with comprehensive user management endpoints
- **Role Hierarchy** (ADMIN, MODERATOR, MERCHANT, CUSTOMER) with proper permissions
- **Security Configuration** with proper endpoint protection and role-based access

**Key Features:**
- `@PreAuthorize("hasPermission(#userId, 'user', 'READ') or hasRole('ADMIN')")` annotations
- Custom permission evaluator for user and address operations
- Admin endpoints: `/admin/users`, `/admin/users/{userId}/lock`, `/admin/users/{userId}/role`
- Role-based access: ADMIN (full access), MODERATOR (read + manage), MERCHANT/CUSTOMER (own data)
- Comprehensive RBAC testing with 11 test cases covering all scenarios

### User Registration Enhancement (Commit 8) ✅
**Complete user registration system with email verification and account activation:**
- **Email Verification System** using secure UUID tokens with configurable expiry
- **Account Activation Workflow** with proper security measures
- **Welcome Email Functionality** with professional HTML templates
- **Comprehensive Validation** including password confirmation and terms acceptance
- **Token Management** with automatic expiry, security features, and cleanup

**Key Features:**
- Complete registration workflow: `/auth/register`, `/auth/verify`, `/auth/activate`
- Email verification with secure tokens and expiry management
- HTML email templates for verification, welcome, and password reset
- Comprehensive validation with custom annotations and business rules
- Token security with UUID generation, expiry tracking, and usage validation
- Full test coverage with 17 test cases covering all scenarios
- RESTful endpoints for registration, verification, and availability checks

## Requirements Breakdown

### Phase 1: Foundation & Security (Critical)
**Target: 5 small commits**

#### Commit 1: Fix Password Security ✅ **COMPLETED**
- ✅ **COMPLETED: Implement proper BCrypt password encoding**
- ✅ **COMPLETED: Fix password validation in login**
- ✅ **COMPLETED: Add password strength validation**
- **Files**: `SubjectAuthServiceImpl.java`, `SpringSecurityConfig.java`

#### Commit 2: Enhance User Entity & Role System
- [ ] Add ecommerce-specific roles (CUSTOMER, MERCHANT, ADMIN)
- [ ] Add user profile fields (firstName, lastName, phone, createdAt, etc.)
- [ ] Add proper JPA annotations and constraints
- **Files**: `User.java`, `Role.java`

#### Commit 3: OAuth2 Client Configuration ✅ **COMPLETED** 
- ✅ **COMPLETED: Configure OAuth2 Authorization Server with JWT tokens**
- ✅ **COMPLETED: Add OAuth2 client registration in database**
- ✅ **COMPLETED: Create client management endpoints**
- ✅ **COMPLETED: Implement client credentials flow for service-to-service auth**
- ✅ **COMPLETED: Add token introspection and validation endpoints**
- ✅ **COMPLETED: Configure Product Service client for authentication**
- **Files**: `SpringSecurityConfig.java`, `ClientController.java`, `schema.sql`, `data.sql`
- **Endpoints**: 
  - `/.well-known/oauth-authorization-server` - Server metadata
  - `/oauth2/token` - Token generation (client credentials)
  - `/oauth2/introspect` - Token validation
  - `/oauth2/jwks` - JWT signature verification keys
  - `/clients/bootstrap/*` - Client registration helpers

#### Commit 4: Comprehensive Error Handling
- [ ] Create custom exception classes
- [ ] Add global exception handler
- [ ] Standardize API response format
- **Files**: `GlobalExceptionHandler.java`, `ApiResponse.java`, `ErrorCodes.java`

#### Commit 5: Input Validation & DTOs ✅ **COMPLETED**
- ✅ **COMPLETED: Add proper validation annotations**
- ✅ **COMPLETED: Create comprehensive DTOs**
- ✅ **COMPLETED: Add request/response validation**
- ✅ **COMPLETED: Custom validation annotations (@StrongPassword, @ValidUsername)**
- ✅ **COMPLETED: Validation utilities and business rules**
- ✅ **COMPLETED: Enhanced controllers with @Valid annotations**
- ✅ **COMPLETED: Comprehensive testing (16 test cases)**
- ✅ **COMPLETED: Complete documentation and README**
- **Files**: `AuthRequestDto.java`, `ClientRegistrationDto.java`, `ClientResponseDto.java`, `LogoutRequestDto.java`, `UserUpdateRequestDto.java`, `PasswordChangeRequestDto.java`, `UserResponseDto.java`, `UserSearchRequestDto.java`, `PaginatedResponseDto.java`, `BaseResponseDto.java`, `AuthResponseDto.java`, validation classes, `ValidationUtil.java`

### Phase 2: User Management Enhancement (High Priority)
**Target: 4 small commits**

#### Commit 6: User Profile Management
- [ ] Add user profile CRUD operations
- [ ] Add address management
- [ ] Add user preferences
- **Files**: `UserController.java`, `UserService.java`, `Address.java`

#### Commit 7: Role-Based Access Control ✅ **COMPLETED**
- ✅ **COMPLETED: Implement method-level security with @PreAuthorize annotations**
- ✅ **COMPLETED: Add permission-based access control with custom permission evaluator**
- ✅ **COMPLETED: Create comprehensive admin endpoints for user management**
- ✅ **COMPLETED: Enhanced security configuration with role-based access**
- ✅ **COMPLETED: Comprehensive RBAC testing (11 test cases)**
- **Files**: `SpringSecurityConfig.java`, `AdminController.java`, `CustomPermissionEvaluator.java`, `@PreAuthorize` annotations
- **Key Features**:
  - Method-level security with `@PreAuthorize("hasPermission(#userId, 'user', 'READ') or hasRole('ADMIN')")`
  - Custom permission evaluator for fine-grained access control
  - Admin endpoints: `/admin/users`, `/admin/users/{userId}/lock`, `/admin/users/{userId}/role`, etc.
  - Role hierarchy: ADMIN (full access), MODERATOR (read + manage), MERCHANT/CUSTOMER (own data only)
  - Security matchers for `/users/**` and `/admin/**` endpoints

#### Commit 8: User Registration Enhancement ✅ **COMPLETED**
- ✅ **COMPLETED: Add email verification system with secure tokens**
- ✅ **COMPLETED: Add account activation workflow**
- ✅ **COMPLETED: Add welcome email functionality with HTML templates**
- ✅ **COMPLETED: Comprehensive registration validation and error handling**
- ✅ **COMPLETED: Token management with expiry and security features**
- ✅ **COMPLETED: Full test coverage (17 test cases)**
- **Files**: `RegistrationService.java`, `EmailService.java`, `VerificationToken.java`, `RegistrationController.java`
- **Key Features**:
  - Complete user registration workflow with validation
  - Email verification using secure UUID tokens with expiry
  - Account activation system
  - HTML email templates for verification, welcome, and password reset
  - Token management with automatic expiry and security
  - Comprehensive error handling and user feedback
  - RESTful endpoints for registration, verification, and activation

#### Commit 9: Session Management ✅ **COMPLETED**
- ✅ **COMPLETED: Enhance session tracking**
- ✅ **COMPLETED: Add session expiry handling**
- ✅ **COMPLETED: Add concurrent session control**
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
