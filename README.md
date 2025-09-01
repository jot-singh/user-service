# User Service - Complete Developer Guide 🚀

## 🎯 Overview

The **User Service** is a comprehensive authentication and authorization microservice built with **Spring Boot 3.2.4** for an ecommerce platform. It provides secure user management, OAuth2/OIDC authentication, and service-to-service communication capabilities.

![Service Architecture](https://via.placeholder.com/800x400/4CAF50/FFFFFF?text=User+Service+Architecture)

---

## 🏗️ System Architecture

### Core Components

```
┌─────────────────────────────────────────────────────────────┐
│                    User Service                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Controllers Layer                   │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │    │
│  │  │ Auth    │ │ User    │ │ Client  │ │ Admin   │   │    │
│  │  │Controller│ │Controller│ │Controller│ │Controller│   │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Services Layer                      │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │    │
│  │  │ Auth    │ │ User    │ │ JWT     │ │ OAuth2  │   │    │
│  │  │ Service │ │ Service │ │ Service │ │ Service │   │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Data Layer                          │    │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │    │
│  │  │ User    │ │ Address │ │ Session │ │ Token   │   │    │
│  │  │ Entity  │ │ Entity  │ │ Entity  │ │ Entity  │   │    │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘   │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| 🏗️ Framework | Spring Boot | 3.2.4 |
| 🔐 Security | Spring Security | 6.2.3 |
| 🔑 OAuth2 | Spring OAuth2 Auth Server | 1.2.3 |
| 🗄️ Database | H2/MySQL | Latest |
| 📊 ORM | Hibernate/JPA | 6.4.4 |
| 🔧 Build Tool | Maven | 3.x |
| ☕ Java | OpenJDK | 17 |

---

## 🔄 Authentication Flows

### 1. User Authentication Flow

```mermaid
sequenceDiagram
    participant U as User
    participant AC as AuthController
    participant AS as AuthService
    participant UD as UserDao
    participant DB as Database
    participant JT as JWT Service

    U->>AC: POST /auth/login
    AC->>AS: login(authRequest)
    AS->>UD: findByUsernameOrEmail()
    UD->>DB: SELECT * FROM users
    DB-->>UD: User data
    UD-->>AS: User entity
    AS->>JT: generateToken(user)
    JT-->>AS: JWT token
    AS-->>AC: AuthResponseDto
    AC-->>U: 200 OK + JWT
```

### 2. OAuth2 Authorization Code Flow

```mermaid
sequenceDiagram
    participant C as Client App
    participant AS as Auth Server
    participant U as User
    participant DB as Database

    C->>AS: GET /oauth2/authorize?response_type=code&client_id=...
    AS->>U: Redirect to login page
    U->>AS: Login credentials
    AS->>DB: Validate user
    AS->>U: Consent screen
    U->>AS: Grant permission
    AS->>C: Redirect with auth code
    C->>AS: POST /oauth2/token (code + client_secret)
    AS->>DB: Validate code
    AS-->>C: Access token + Refresh token
```

### 3. Service-to-Service Flow

```mermaid
sequenceDiagram
    participant PS as Product Service
    participant US as User Service
    participant DB as Database

    PS->>US: POST /oauth2/token
    Note over PS,US: client_credentials grant
    US->>DB: Validate client
    DB-->>US: Client data
    US-->>PS: Access token
    PS->>US: GET /users/validate (with token)
    US->>US: Validate JWT
    US-->>PS: User info
```

---

## 📊 Data Model

### User Entity Relationship

```mermaid
erDiagram
    USER ||--o{ ADDRESS : has
    USER ||--o{ SESSION : creates
    USER ||--o{ VERIFICATION_TOKEN : receives
    USER }o--|| ROLE : belongs_to

    USER {
        bigint id PK
        varchar username UK
        varchar password
        varchar email UK
        varchar first_name
        varchar last_name
        varchar phone
        enum role
        boolean email_verified
        boolean account_locked
        int failed_login_attempts
        datetime created_at
        datetime updated_at
        datetime last_login
    }

    ADDRESS {
        bigint id PK
        bigint user_id FK
        varchar street
        varchar city
        varchar state
        varchar zip_code
        varchar country
        enum type
    }

    SESSION {
        bigint id PK
        bigint user_id FK
        varchar session_id
        datetime expires_at
        varchar ip_address
        varchar user_agent
    }

    VERIFICATION_TOKEN {
        bigint id PK
        bigint user_id FK
        varchar token
        datetime expires_at
        enum type
    }

    ROLE {
        enum name PK "CUSTOMER, MERCHANT, ADMIN"
        varchar description
    }
```

---

## 🔗 API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/auth/login` | User login | ❌ No |
| `POST` | `/auth/signUp` | User registration | ❌ No |
| `POST` | `/auth/logout` | User logout | ✅ Yes |
| `GET` | `/auth/verify` | Email verification | ❌ No |
| `PUT` | `/auth/update/{userId}` | Update user profile | ✅ Yes |

### User Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/users/profile` | Get user profile | ✅ Yes |
| `PUT` | `/users/profile` | Update user profile | ✅ Yes |
| `POST` | `/users/address` | Add user address | ✅ Yes |
| `GET` | `/users/addresses` | Get user addresses | ✅ Yes |
| `PUT` | `/users/address/{id}` | Update address | ✅ Yes |
| `DELETE` | `/users/address/{id}` | Delete address | ✅ Yes |

### OAuth2 Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/.well-known/oauth-authorization-server` | Server metadata | ❌ No |
| `GET` | `/oauth2/authorize` | Authorization endpoint | ❌ No |
| `POST` | `/oauth2/token` | Token endpoint | ❌ No |
| `POST` | `/oauth2/introspect` | Token introspection | ✅ Client |
| `GET` | `/oauth2/jwks` | JWK Set | ❌ No |
| `POST` | `/oauth2/revoke` | Token revocation | ✅ Client |

### Client Management Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/clients/register` | Register OAuth2 client | ✅ Admin |
| `GET` | `/clients/{clientId}` | Get client info | ✅ Admin |
| `GET` | `/clients` | List all clients | ✅ Admin |
| `DELETE` | `/clients/{clientId}` | Delete client | ✅ Admin |
| `POST` | `/clients/bootstrap/product-service` | Bootstrap product service client | ❌ No |

---

## 🔐 Security Configuration

### Spring Security Filter Chain

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    // 1. OAuth2 Authorization Server (Order 1)
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // OAuth2 endpoints: /oauth2/authorize, /oauth2/token, etc.
    }

    // 2. Application Security (Order 2)
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/login", "/auth/signUp").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/users/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
```

### JWT Token Structure

```json
{
  "alg": "RS256",
  "typ": "JWT"
}
{
  "iss": "http://localhost:8444",
  "sub": "user123",
  "aud": "product-service",
  "exp": 1693526400,
  "iat": 1693522800,
  "scope": "read write",
  "authorities": ["ROLE_CUSTOMER"],
  "client_id": "product-service"
}
```

---

## 🚀 Getting Started

### Prerequisites

- ☕ **Java 17** or higher
- 🏗️ **Maven 3.6+**
- 🗄️ **H2 Database** (included) or **MySQL**

### Quick Start

```bash
# 1. Clone the repository
git clone <repository-url>
cd user-service

# 2. Run with Maven
./mvnw spring-boot:run

# 3. Service will start on http://localhost:8444
```

### Configuration Profiles

```yaml
# application-local.properties (H2 Database)
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:h2:file:./userdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update

# application-dev.properties (MySQL Database)
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/userdb
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 🧪 Testing the Service

### 1. Bootstrap Product Service Client

```bash
curl -X POST "http://localhost:8444/clients/bootstrap/product-service"
```

### 2. Get Access Token (Client Credentials)

```bash
curl -X POST http://localhost:8444/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=user.read" \
  --user "product-service:product-service-secret"
```

### 3. User Registration

```bash
curl -X POST "http://localhost:8444/auth/signUp" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 4. User Login

```bash
curl -X POST "http://localhost:8444/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePass123!"
  }'
```

### 5. Access Protected Resource

```bash
curl -X GET "http://localhost:8444/users/profile" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🔧 Development Workflow

### Project Structure

```
user-service/
├── src/main/java/com/user/service/
│   ├── UserServiceApplication.java          # Main application class
│   ├── conf/                                # Configuration classes
│   ├── controller/                          # REST controllers
│   │   ├── AuthController.java             # Authentication endpoints
│   │   ├── UserController.java             # User management
│   │   ├── ClientController.java           # OAuth2 client management
│   │   └── AdminController.java            # Admin operations
│   ├── dto/                                # Data Transfer Objects
│   │   ├── request/                        # Request DTOs
│   │   └── response/                       # Response DTOs
│   ├── entity/                             # JPA entities
│   │   ├── User.java                       # User entity
│   │   ├── Address.java                    # Address entity
│   │   ├── Session.java                    # Session entity
│   │   └── Token.java                      # Token entity
│   ├── repository/                         # JPA repositories
│   ├── security/                           # Security configuration
│   │   ├── SpringSecurityConfig.java       # Main security config
│   │   ├── jwt/                           # JWT utilities
│   │   └── models/                        # Security models
│   ├── services/                           # Business logic
│   ├── util/                               # Utility classes
│   ├── validation/                         # Validation logic
│   └── error/                              # Error handling
├── src/main/resources/
│   ├── application.properties              # Main configuration
│   ├── application-local.properties        # Local profile
│   ├── application-dev.properties          # Dev profile
│   ├── data.sql                           # Initial data
│   └── schema.sql                         # Database schema
└── src/test/                               # Test classes
```

### Key Classes Overview

#### Controllers

1. **AuthController** - Handles user authentication
   - `POST /auth/login` - User login
   - `POST /auth/signUp` - User registration
   - `POST /auth/logout` - User logout

2. **UserController** - User profile management
   - `GET /users/profile` - Get profile
   - `PUT /users/profile` - Update profile
   - `POST /users/address` - Add address

3. **ClientController** - OAuth2 client management
   - `POST /clients/register` - Register client
   - `GET /clients/{id}` - Get client info
   - `POST /clients/bootstrap/product-service` - Bootstrap client

#### Services

1. **AuthService** - Authentication business logic
2. **UserService** - User management operations
3. **JwtService** - JWT token operations

#### Entities

1. **User** - Main user entity with profile info
2. **Address** - User addresses
3. **Session** - User sessions
4. **Token** - Verification tokens

---

## 📈 Monitoring & Logging

### Health Check Endpoints

```bash
# Application health
curl http://localhost:8444/actuator/health

# Application info
curl http://localhost:8444/actuator/info

# Metrics
curl http://localhost:8444/actuator/metrics
```

### Logging Configuration

```properties
# application.properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
logging.level.com.user.service=INFO

# File logging
logging.file.name=logs/user-service.log
logging.logback.rollingpolicy.max-file-size=10MB
```

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Database Connection Issues

**Problem:** `Connection refused` or `Table not found`

**Solution:**
```bash
# Check H2 console
open http://localhost:8444/h2-console

# JDBC URL: jdbc:h2:file:./userdb
# Username: sa
# Password: (leave empty)
```

#### 2. OAuth2 Client Not Found

**Problem:** `Invalid client` error

**Solution:**
```bash
# Bootstrap the client
curl -X POST "http://localhost:8444/clients/bootstrap/product-service"

# Verify client exists
curl -X GET "http://localhost:8444/clients/product-service"
```

#### 3. JWT Token Issues

**Problem:** `Invalid token` or `Token expired`

**Solution:**
```bash
# Check token structure
curl -X POST "http://localhost:8444/oauth2/introspect" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=YOUR_TOKEN" \
  --user "product-service:product-service-secret"
```

---

## 📚 API Documentation

### Swagger UI

Access the interactive API documentation at:
```
http://localhost:8444/swagger-ui.html
```

### OpenAPI Specification

The service provides comprehensive OpenAPI 3.0 documentation with:
- Detailed endpoint descriptions
- Request/response schemas
- Authentication requirements
- Example requests

---

## 🎯 Best Practices

### Security Best Practices

1. **Password Policies**
   - Minimum 8 characters
   - Mix of uppercase, lowercase, numbers, symbols
   - No common passwords

2. **Token Management**
   - Short-lived access tokens (1 hour)
   - Secure refresh token storage
   - Regular token rotation

3. **Input Validation**
   - Server-side validation on all inputs
   - Sanitize user inputs
   - Use parameterized queries

### Performance Best Practices

1. **Database Optimization**
   - Use appropriate indexes
   - Connection pooling
   - Query optimization

2. **Caching Strategy**
   - Cache user sessions
   - Cache OAuth2 tokens
   - Cache frequently accessed data

3. **Monitoring**
   - Application metrics
   - Database performance
   - Security events logging

---

## 🚀 Deployment

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8444
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: user-service:latest
        ports:
        - containerPort: 8444
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

---

## 📞 Support & Resources

### Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [JWT.io](https://jwt.io/) - JWT debugger
- [OAuth2 Specification](https://tools.ietf.org/html/rfc6749)

### Getting Help

1. **Check the logs** - Enable debug logging for troubleshooting
2. **Use H2 Console** - Inspect database state
3. **Test endpoints** - Use the provided curl commands
4. **Review configuration** - Check application properties

---

## 🎉 Success Metrics

✅ **Completed Features:**
- OAuth2 Authorization Server with JWT
- User registration and authentication
- Role-based access control (RBAC)
- Comprehensive input validation
- Session management
- Address management
- Client management
- Service-to-service authentication

🚧 **Next Steps:**
- API documentation completion
- Advanced user features
- Integration testing
- Performance optimization

---

*This documentation provides a comprehensive guide for developers to understand, develop, and maintain the User Service. For any questions or contributions, please refer to the project repository.*</content>
<parameter name="filePath">/Users/admin/IdeaProjects/ecommerce/user-service/README.md
