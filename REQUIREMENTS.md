# User Service Requirements for Ecommerce Platform

## Project Overview
Complete the User Service to provide authentication, authorization, and user management for an ecommerce platform where the Product Service will consume user authentication services.

## Current State Analysis
- ✅ Basic Spring Boot setup with Spring Security
- ✅ JWT token generation and validation
- ✅ **COMPLETED: OAuth2 Authorization Server configuration**
- ✅ Simple user authentication (login/signup/logout)
- ✅ **COMPLETED: H2 and MySQL database integration with profiles**
- ❌ Missing proper password encryption
- ❌ Missing comprehensive user management
- ✅ **COMPLETED: OAuth2 client configuration for Product Service**
- ❌ Missing role-based access control
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
