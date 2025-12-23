# Local Setup and Testing Guide 🚀

Complete guide to boot both User Service and Product Service locally and test the complete e-commerce flows.

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Database Configuration](#database-configuration)
4. [Starting the Services](#starting-the-services)
5. [Testing Authentication Flow](#testing-authentication-flow)
6. [Testing Product Service Flow](#testing-product-service-flow)
7. [Testing Complete E-commerce Flow](#testing-complete-e-commerce-flow)
8. [Common Issues and Solutions](#common-issues-and-solutions)
9. [Environment Variables Reference](#environment-variables-reference)

---

## Prerequisites

Ensure you have the following installed on your local machine:

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| ☕ Java | 17+ | Running Spring Boot applications |
| 🏗️ Maven | 3.6+ | Building projects |
| 🗄️ MySQL | 8.0+ | Database for both services |
| 🌐 curl/Postman | Latest | API testing |
| 🐚 Terminal | - | Running commands |

### Optional Software

| Software | Version | Purpose |
|----------|---------|---------|
| 🐳 Docker | Latest | Container-based setup |
| 🔍 Elasticsearch | 8.8.2 | Product search (optional) |
| 📦 Redis | 7.2 | Caching (optional) |

### Verify Installation

```bash
# Check Java version
java -version
# Expected: openjdk version "17" or higher

# Check Maven version
mvn -version
# Expected: Apache Maven 3.6.0 or higher

# Check MySQL
mysql --version
# Expected: mysql Ver 8.0.x
```

---

## Initial Setup

### Step 1: Clone and Navigate to Projects

```bash
cd /Users/admin/Downloads/ecommerce

# Verify both projects exist
ls -la
# Expected output:
# drwxr-xr-x  product-service-main/
# drwxr-xr-x  user-service-main/
```

### Step 2: Build Both Projects

```bash
# Build User Service
cd user-service-main
./mvnw clean install -DskipTests
cd ..

# Build Product Service
cd product-service-main
./mvnw clean install -DskipTests
cd ..
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

---

## Database Configuration

### Option A: Using MySQL (Recommended for Production-like Testing)

#### 1. Start MySQL Server

```bash
# On macOS
brew services start mysql

# Or if installed differently
mysql.server start
```

#### 2. Create Databases

```bash
# Login to MySQL
mysql -u root -p
# Enter your MySQL root password
```

```sql
-- Create database for User Service
CREATE DATABASE userdb;

-- Create database for Product Service
CREATE DATABASE products;

-- Verify databases
SHOW DATABASES;

-- Exit MySQL
EXIT;
```

#### 3. Configure User Service

Edit `user-service-main/src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/userdb
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Update `user-service-main/src/main/resources/application.properties`:

```properties
# Set active profile to dev (for MySQL)
spring.profiles.active=dev
```

#### 4. Configure Product Service

Edit `product-service-main/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/products
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update

# OAuth2 Resource Server
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444

# Disable optional features for local testing
elasticsearch.enabled=false
redis.enabled=false
```

### Option B: Using H2 (Quick Testing)

For User Service only - simpler setup with in-memory database.

Edit `user-service-main/src/main/resources/application.properties`:

```properties
# Set active profile to local (for H2)
spring.profiles.active=local
```

**Note**: Product Service requires MySQL, so this is only partial H2 setup.

---

## Starting the Services

### Important: Start Order Matters! ⚠️

**ALWAYS start User Service first**, as Product Service depends on it for authentication.

### Step 1: Start User Service (Port 8444)

```bash
cd /Users/admin/Downloads/ecommerce/user-service-main

# Start the service
./mvnw spring-boot:run
```

**Wait for the service to fully start. Look for:**
```
Started UserServiceApplication in X.XXX seconds
```

**Verify User Service is running:**

```bash
# In a new terminal
curl http://localhost:8444/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

**Check OAuth2 configuration:**
```bash
curl http://localhost:8444/.well-known/oauth-authorization-server | jq
```

**Expected Response:**
```json
{
  "issuer": "http://localhost:8444",
  "authorization_endpoint": "http://localhost:8444/oauth2/authorize",
  "token_endpoint": "http://localhost:8444/oauth2/token",
  "jwks_uri": "http://localhost:8444/oauth2/jwks"
}
```

### Step 2: Start Product Service (Port 8080)

```bash
# In a new terminal
cd /Users/admin/Downloads/ecommerce/product-service-main

# Start the service
./mvnw spring-boot:run
```

**Wait for the service to fully start. Look for:**
```
Started ProductServiceApplication in X.XXX seconds
```

**Verify Product Service is running:**

```bash
# In a new terminal
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

### Verify Both Services

```bash
# Check User Service
curl -s http://localhost:8444/actuator/health | jq '.status'
# Expected: "UP"

# Check Product Service
curl -s http://localhost:8080/actuator/health | jq '.status'
# Expected: "UP"
```

---

## Testing Authentication Flow

### Step 1: Register a New User

```bash
curl -X POST http://localhost:8444/auth/signUp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "Test@123456",
    "firstName": "Test",
    "lastName": "User",
    "phone": "+1234567890"
  }' | jq
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "emailVerified": false,
    "role": "CUSTOMER"
  }
}
```

### Step 2: Login and Get JWT Token

```bash
curl -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123456"
  }' | jq
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER",
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Save the token for subsequent requests:**
```bash
# Extract and save token
export JWT_TOKEN=$(curl -s -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123456"
  }' | jq -r '.data.token')

# Verify token is saved
echo $JWT_TOKEN
```

### Step 3: Test Token Validation

```bash
curl -X GET http://localhost:8444/users/profile \
  -H "Authorization: Bearer $JWT_TOKEN" | jq
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "CUSTOMER"
  }
}
```

---

## Testing Product Service Flow

### Step 1: Create a Category

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }' | jq
```

**Expected Response (201 Created):**
```json
{
  "id": "uuid-here",
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

### Step 2: Create a Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "MacBook Pro 16",
    "description": "High-performance laptop with M3 chip",
    "price": 2499.99,
    "image": "https://example.com/macbook.jpg",
    "category": "Electronics"
  }' | jq
```

**Expected Response (201 Created):**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "MacBook Pro 16",
  "description": "High-performance laptop with M3 chip",
  "price": 2499.99,
  "image": "https://example.com/macbook.jpg",
  "category": "Electronics"
}
```

**Save the product ID:**
```bash
export PRODUCT_ID="3fa85f64-5717-4562-b3fc-2c963f66afa6"
```

### Step 3: Get All Products

```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $JWT_TOKEN" | jq
```

### Step 4: Get Single Product

```bash
curl -X GET "http://localhost:8080/api/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" | jq
```

### Step 5: Update Product

```bash
curl -X PUT "http://localhost:8080/api/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "MacBook Pro 16 (Updated)",
    "description": "High-performance laptop with M3 chip - Now with 32GB RAM",
    "price": 2699.99,
    "image": "https://example.com/macbook.jpg",
    "category": "Electronics"
  }' | jq
```

### Step 6: Delete Product

```bash
curl -X DELETE "http://localhost:8080/api/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Expected Response (204 No Content)**

---

## Testing Complete E-commerce Flow

This section demonstrates a complete user journey from registration to order placement.

### Flow 1: Complete Shopping Journey

```bash
# 1. Register a new customer
curl -X POST http://localhost:8444/auth/signUp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "shopper1",
    "email": "shopper1@example.com",
    "password": "Shop@123456",
    "firstName": "John",
    "lastName": "Shopper",
    "phone": "+1234567890"
  }' | jq

# 2. Login and get token
export SHOPPER_TOKEN=$(curl -s -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "shopper1",
    "password": "Shop@123456"
  }' | jq -r '.data.token')

# 3. Browse products
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $SHOPPER_TOKEN" | jq

# 4. Get product details
curl -X GET "http://localhost:8080/api/products/$PRODUCT_ID" \
  -H "Authorization: Bearer $SHOPPER_TOKEN" | jq

# 5. Create an order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $SHOPPER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": "'"$PRODUCT_ID"'",
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "San Francisco",
      "state": "CA",
      "zipCode": "94102",
      "country": "USA"
    }
  }' | jq

# 6. View order history
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $SHOPPER_TOKEN" | jq
```

### Flow 2: Admin Operations

```bash
# 1. Register an admin user (you may need to manually update role in DB)
curl -X POST http://localhost:8444/auth/signUp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "Admin@123456",
    "firstName": "Admin",
    "lastName": "User",
    "phone": "+1234567890"
  }' | jq

# 2. Login as admin
export ADMIN_TOKEN=$(curl -s -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123456"
  }' | jq -r '.data.token')

# 3. Create multiple products
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "iPhone 15 Pro",
    "description": "Latest iPhone with titanium design",
    "price": 999.99,
    "image": "https://example.com/iphone.jpg",
    "category": "Electronics"
  }' | jq

# 4. View all orders (admin privilege)
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq

# 5. Get all users (admin endpoint)
curl -X GET http://localhost:8444/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq
```

### Flow 3: Testing OAuth2 Client Credentials (Service-to-Service)

```bash
# Product Service calling User Service internally
# This happens automatically when Product Service validates tokens

# Test internal communication
curl -X POST http://localhost:8444/oauth2/token \
  -u "product-service:product-service-secret" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=internal.read internal.write" | jq
```

**Expected Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "internal.read internal.write"
}
```

---

## Common Issues and Solutions

### Issue 1: Port Already in Use

**Symptoms:**
```
***************************
APPLICATION FAILED TO START
***************************

Description:
Web server failed to start. Port 8444 was already in use.
```

**Solution:**
```bash
# Find and kill the process using the port
lsof -ti:8444 | xargs kill -9  # For User Service
lsof -ti:8080 | xargs kill -9  # For Product Service

# Or change the port in application.properties
server.port=8445
```

### Issue 2: Database Connection Failed

**Symptoms:**
```
Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
```

**Solutions:**

1. **Check MySQL is running:**
```bash
brew services list | grep mysql
# If not running:
brew services start mysql
```

2. **Verify database exists:**
```bash
mysql -u root -p -e "SHOW DATABASES;"
```

3. **Check credentials in application.properties**

4. **Test connection manually:**
```bash
mysql -u root -p -h localhost -P 3306 products
```

### Issue 3: JWT Token Validation Failed

**Symptoms:**
```json
{
  "error": "invalid_token",
  "error_description": "An error occurred while attempting to decode the Jwt"
}
```

**Solutions:**

1. **Ensure User Service is running first**
2. **Check issuer-uri in Product Service:**
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444
```

3. **Verify User Service OAuth2 endpoint:**
```bash
curl http://localhost:8444/.well-known/oauth-authorization-server
```

4. **Get a fresh token:**
```bash
export JWT_TOKEN=$(curl -s -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "Test@123456"}' \
  | jq -r '.data.token')
```

### Issue 4: Build Failures

**Symptoms:**
```
[ERROR] Failed to execute goal
[ERROR] BUILD FAILURE
```

**Solutions:**

1. **Clean and rebuild:**
```bash
./mvnw clean install -DskipTests
```

2. **Check Java version:**
```bash
java -version
# Must be 17 or higher
```

3. **Update Maven wrapper:**
```bash
./mvnw wrapper:wrapper
```

### Issue 5: 403 Forbidden Error

**Symptoms:**
```json
{
  "error": "insufficient_scope",
  "error_description": "The request requires higher privileges"
}
```

**Solutions:**

1. **Check token is included in request:**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $JWT_TOKEN"
# NOT: -H "Authorization: $JWT_TOKEN"
```

2. **Verify token hasn't expired (valid for 1 hour by default)**

3. **Check user role/permissions**

---

## Environment Variables Reference

### User Service Environment Variables

```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=userdb
export DB_USERNAME=root
export DB_PASSWORD=your_password

# Server Configuration
export SERVER_PORT=8444

# JWT Configuration
export JWT_EXPIRATION=3600000  # 1 hour in milliseconds

# OAuth2 Configuration
export OAUTH2_ISSUER=http://localhost:8444
```

### Product Service Environment Variables

```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=products
export DB_USERNAME=root
export DB_PASSWORD=your_password

# Server Configuration
export SERVER_PORT=8080

# OAuth2 Resource Server
export JWT_ISSUER_URI=http://localhost:8444

# Optional Features
export ELASTICSEARCH_ENABLED=false
export REDIS_ENABLED=false

# OAuth2 Client (for service-to-service)
export OAUTH2_CLIENT_ID=product-service
export OAUTH2_CLIENT_SECRET=product-service-secret
export OAUTH2_TOKEN_URI=http://localhost:8444/oauth2/token
```

### Using Environment Variables

You can create a `.env` file or export them in your shell:

```bash
# Create .env file
cat > .env << 'EOF'
DB_PASSWORD=your_password
JWT_EXPIRATION=3600000
EOF

# Load environment variables
export $(cat .env | xargs)
```

---

## Quick Reference - Essential Commands

### Starting Services
```bash
# Terminal 1 - User Service
cd user-service-main && ./mvnw spring-boot:run

# Terminal 2 - Product Service
cd product-service-main && ./mvnw spring-boot:run
```

### Getting a Token
```bash
export JWT_TOKEN=$(curl -s -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "Test@123456"}' \
  | jq -r '.data.token')
```

### Testing Endpoints
```bash
# Health Check
curl http://localhost:8444/actuator/health
curl http://localhost:8080/actuator/health

# Get Products
curl -H "Authorization: Bearer $JWT_TOKEN" http://localhost:8080/api/products | jq

# Get User Profile
curl -H "Authorization: Bearer $JWT_TOKEN" http://localhost:8444/users/profile | jq
```

### Stopping Services
```bash
# Press Ctrl+C in each terminal running the services

# Or kill by port
lsof -ti:8444 | xargs kill -9
lsof -ti:8080 | xargs kill -9
```

---

## Testing Tools

### Using Postman

1. **Import Collection**: Create a new collection with the following structure:

```
E-commerce Testing
├── User Service
│   ├── Register User
│   ├── Login
│   ├── Get Profile
│   └── Logout
└── Product Service
    ├── Create Product
    ├── Get All Products
    ├── Get Product by ID
    ├── Update Product
    ├── Delete Product
    └── Create Order
```

2. **Set Collection Variables**:
   - `baseUrl_user`: `http://localhost:8444`
   - `baseUrl_product`: `http://localhost:8080`
   - `token`: (set from login response)

3. **Add Authorization**:
   - Type: Bearer Token
   - Token: `{{token}}`

### Using VS Code REST Client

Install the REST Client extension and create `test-requests.http`:

```http
### Variables
@baseUrlUser = http://localhost:8444
@baseUrlProduct = http://localhost:8080
@token = your-token-here

### Register User
POST {{baseUrlUser}}/auth/signUp
Content-Type: application/json

{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "Test@123456",
  "firstName": "Test",
  "lastName": "User"
}

### Login
POST {{baseUrlUser}}/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test@123456"
}

### Get Products
GET {{baseUrlProduct}}/api/products
Authorization: Bearer {{token}}
```

---

## Next Steps

After successfully testing locally:

1. **Explore API Documentation**:
   - User Service: [API_SPECIFICATION.md](user-service-main/API_SPECIFICATION.md)
   - Product Service: [API_SPECIFICATION.md](product-service-main/API_SPECIFICATION.md)

2. **Review Architecture**:
   - [Product Service Architecture](product-service-main/ARCHITECTURE_DIAGRAMS.md)
   - [User Service Architecture](user-service-main/ARCHITECTURE_DIAGRAMS.md)

3. **Enable Optional Features**:
   - Elasticsearch for product search
   - Redis for caching
   - Stripe for payment processing

4. **Deploy to Production**:
   - Review Docker configuration
   - Set up environment-specific properties
   - Configure production databases

---

## Support and Troubleshooting

For more detailed troubleshooting:
- [Product Service Troubleshooting](product-service-main/TROUBLESHOOTING.md)
- [User Service Troubleshooting](user-service-main/TROUBLESHOOTING.md)

---

**Happy Testing! 🎉**

Last Updated: December 23, 2025
