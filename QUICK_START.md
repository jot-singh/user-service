# User Service Quick Start Guide 🚀

## Prerequisites

Before you begin, ensure you have the following installed:

- ☕ **Java 17** or higher
- 🏗️ **Maven 3.6+**
- 🌐 **curl** or **Postman** for testing
- 🐚 **Terminal/Command Prompt**

---

## 1. Project Setup

### Clone and Navigate
```bash
cd /path/to/your/projects
git clone <repository-url>
cd user-service
```

### Verify Project Structure
```bash
ls -la
```
Expected output:
```
drwxr-xr-x  user-service/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── target/
└── README.md
```

---

## 2. Configuration

### Environment Profiles

The service supports multiple database profiles:

#### Local Development (H2)
```properties
# src/main/resources/application-local.properties
spring.profiles.active=local
spring.datasource.url=jdbc:h2:file:./userdb
spring.jpa.hibernate.ddl-auto=update
```

#### Production (MySQL)
```properties
# src/main/resources/application-dev.properties
spring.profiles.active=dev
spring.datasource.url=jdbc:mysql://localhost:3306/userdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Main Configuration
```properties
# src/main/resources/application.properties
spring.application.name=userservice
server.port=8444
logging.level.org.springframework.security=DEBUG
jwt.expiration=3600000
```

---

## 3. Build and Run

### Build the Application
```bash
./mvnw clean compile
```

### Run the Application
```bash
./mvnw spring-boot:run
```

### Verify Service is Running
```bash
curl -s http://localhost:8444/.well-known/oauth-authorization-server | head -5
```

Expected response:
```json
{
  "issuer": "http://localhost:8444",
  "authorization_endpoint": "http://localhost:8444/oauth2/authorize",
  "token_endpoint": "http://localhost:8444/oauth2/token",
  ...
}
```

---

## 4. Bootstrap OAuth2 Client

### Create Product Service Client
```bash
curl -X POST "http://localhost:8444/clients/bootstrap/product-service"
```

**Response:**
```json
{
  "clientId": "product-service",
  "clientSecret": "product-service-secret",
  "clientName": "Product Service",
  "scopes": ["user.read", "user.validate", "read", "write", "openid", "profile"],
  "redirectUris": ["http://localhost:3000/callback"]
}
```

---

## 5. Test User Registration

### Register a New User
```bash
curl -X POST "http://localhost:8444/auth/signUp" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "CUSTOMER"
  }
}
```

---

## 6. Test User Authentication

### User Login
```bash
curl -X POST "http://localhost:8444/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "SecurePass123!"
  }'
```

**Success Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "testuser",
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Save the JWT token** for the next steps:
```bash
export JWT_TOKEN="your_jwt_token_here"
```

---

## 7. Test Protected Endpoints

### Get User Profile
```bash
curl -X GET "http://localhost:8444/users/profile" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER",
    "emailVerified": false
  }
}
```

### Update User Profile
```bash
curl -X PUT "http://localhost:8444/users/profile" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Name"
  }'
```

### Add User Address
```bash
curl -X POST "http://localhost:8444/users/address" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "type": "HOME"
  }'
```

### Get User Addresses
```bash
curl -X GET "http://localhost:8444/users/addresses" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 8. Test OAuth2 Flows

### Client Credentials Flow
```bash
curl -X POST "http://localhost:8444/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=user.read" \
  --user "product-service:product-service-secret"
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "user.read"
}
```

### Token Introspection
```bash
curl -X POST "http://localhost:8444/oauth2/introspect" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$ACCESS_TOKEN" \
  --user "product-service:product-service-secret"
```

**Response:**
```json
{
  "active": true,
  "client_id": "product-service",
  "scope": "user.read",
  "token_type": "Bearer",
  "exp": 1693526400,
  "iat": 1693522800
}
```

---

## 9. Database Inspection

### Access H2 Console
1. Open browser: `http://localhost:8444/h2-console`
2. JDBC URL: `jdbc:h2:file:./userdb`
3. Username: `sa`
4. Password: *(leave empty)*

### Check Database Tables
```sql
SHOW TABLES;

SELECT * FROM users;
SELECT * FROM addresses;
SELECT * FROM sessions;
SELECT * FROM verification_token;
```

---

## 10. Testing with Postman

### Import API Collection
```json
{
  "info": {
    "name": "User Service API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "User Registration",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"email\": \"test@example.com\",\n  \"password\": \"SecurePass123!\",\n  \"firstName\": \"Test\",\n  \"lastName\": \"User\"\n}"
            },
            "url": {
              "raw": "http://localhost:8444/auth/signUp",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8444",
              "path": ["auth", "signUp"]
            }
          }
        },
        {
          "name": "User Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"SecurePass123!\"\n}"
            },
            "url": {
              "raw": "http://localhost:8444/auth/login",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8444",
              "path": ["auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "User Management",
      "item": [
        {
          "name": "Get Profile",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwt_token}}"
              }
            ],
            "url": {
              "raw": "http://localhost:8444/users/profile",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8444",
              "path": ["users", "profile"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "jwt_token",
      "value": "",
      "type": "string"
    }
  ]
}
```

---

## 11. Common Testing Scenarios

### ✅ Successful User Journey
1. Register user
2. Login and get JWT
3. Update profile
4. Add address
5. Get addresses
6. Logout

### ✅ OAuth2 Client Credentials Flow
1. Bootstrap client
2. Get access token
3. Validate token
4. Use token for API calls

### ✅ Error Scenarios
1. Invalid credentials
2. Expired token
3. Insufficient permissions
4. Invalid input data

---

## 12. Development Tips

### Useful Commands

```bash
# Run with different profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test

# Build JAR
./mvnw clean package

# Run JAR
java -jar target/user-service-0.0.1-SNAPSHOT.jar

# Check application health
curl http://localhost:8444/actuator/health

# View application logs
tail -f logs/user-service.log
```

### Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=dev
export SERVER_PORT=8444
export JWT_EXPIRATION=3600000
```

---

## 13. Next Steps

After completing this quick start:

1. **Explore the API** - Use Swagger UI at `http://localhost:8444/swagger-ui.html`
2. **Add more users** - Test with different roles (CUSTOMER, MERCHANT, ADMIN)
3. **Test OAuth2 flows** - Try authorization code flow with a frontend app
4. **Monitor performance** - Check application metrics
5. **Add custom validations** - Extend the validation framework
6. **Implement caching** - Add Redis for session storage
7. **Set up monitoring** - Configure Prometheus and Grafana

---

## 🎯 Quick Reference

| Action | Command |
|--------|---------|
| Start service | `./mvnw spring-boot:run` |
| Bootstrap client | `curl -X POST http://localhost:8444/clients/bootstrap/product-service` |
| Register user | `curl -X POST http://localhost:8444/auth/signUp -H "Content-Type: application/json" -d '{"username":"test","email":"test@example.com","password":"Pass123!"}'` |
| Login user | `curl -X POST http://localhost:8444/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"Pass123!"}'` |
| Get profile | `curl -X GET http://localhost:8444/users/profile -H "Authorization: Bearer <token>"` |
| H2 Console | `http://localhost:8444/h2-console` |
| API Docs | `http://localhost:8444/swagger-ui.html` |

---

*This quick start guide gets you up and running with the User Service in minutes. Follow the steps sequentially and you'll have a fully functional authentication and user management system!* 🚀</content>
<parameter name="filePath">/Users/admin/IdeaProjects/ecommerce/user-service/QUICK_START.md
