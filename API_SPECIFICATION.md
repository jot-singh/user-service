# User Service API Specification 📋

## Overview
Complete API specification for the User Service with detailed endpoint documentation, request/response schemas, and authentication requirements.

---

## 🔐 Authentication

### Supported Authentication Methods

| Method | Description | Use Case |
|--------|-------------|----------|
| **Bearer Token** | JWT access token | User API calls |
| **Basic Auth** | Client credentials | OAuth2 client authentication |
| **No Auth** | Public endpoints | Registration, metadata |

### JWT Token Structure

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT"
  },
  "payload": {
    "iss": "http://localhost:8444",
    "sub": "user123",
    "aud": "product-service",
    "exp": 1693526400,
    "iat": 1693522800,
    "scope": "read write",
    "authorities": ["ROLE_CUSTOMER"],
    "client_id": "product-service"
  },
  "signature": "RS256_signature..."
}
```

---

## 📍 API Endpoints

### 1. Authentication Endpoints

#### POST `/auth/login`
User authentication endpoint.

**Request:**
```json
{
  "username": "johndoe",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CUSTOMER",
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**Error Response (400/401):**
```json
{
  "success": false,
  "message": "Invalid credentials",
  "error": {
    "code": "AUTH001",
    "field": "password"
  }
}
```

#### POST `/auth/signUp`
User registration endpoint.

**Request:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "emailVerified": false,
    "role": "CUSTOMER"
  }
}
```

#### POST `/auth/logout`
User logout endpoint.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

### 2. User Management Endpoints

#### GET `/users/profile`
Get current user profile.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "role": "CUSTOMER",
    "emailVerified": true,
    "accountLocked": false,
    "createdAt": "2025-01-01T10:00:00Z",
    "updatedAt": "2025-01-01T10:00:00Z",
    "lastLogin": "2025-01-01T10:00:00Z"
  }
}
```

#### PUT `/users/profile`
Update user profile.

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+1987654321"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": 123,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Smith",
    "phone": "+1987654321"
  }
}
```

#### POST `/users/address`
Add user address.

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request:**
```json
{
  "street": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "type": "HOME"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Address added successfully",
  "data": {
    "addressId": 456,
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "type": "HOME"
  }
}
```

#### GET `/users/addresses`
Get user addresses.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "addressId": 456,
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA",
      "type": "HOME"
    },
    {
      "addressId": 457,
      "street": "456 Office Blvd",
      "city": "New York",
      "state": "NY",
      "zipCode": "10002",
      "country": "USA",
      "type": "WORK"
    }
  ]
}
```

### 3. OAuth2 Endpoints

#### GET `/.well-known/oauth-authorization-server`
OAuth2 authorization server metadata.

**Response (200 OK):**
```json
{
  "issuer": "http://localhost:8444",
  "authorization_endpoint": "http://localhost:8444/oauth2/authorize",
  "device_authorization_endpoint": "http://localhost:8444/oauth2/device_authorization",
  "token_endpoint": "http://localhost:8444/oauth2/token",
  "token_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt"
  ],
  "jwks_uri": "http://localhost:8444/oauth2/jwks",
  "response_types_supported": ["code"],
  "grant_types_supported": [
    "authorization_code",
    "client_credentials",
    "refresh_token",
    "urn:ietf:params:oauth:grant-type:device_code"
  ],
  "revocation_endpoint": "http://localhost:8444/oauth2/revoke",
  "revocation_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt"
  ],
  "introspection_endpoint": "http://localhost:8444/oauth2/introspect",
  "introspection_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt"
  ],
  "code_challenge_methods_supported": ["S256"]
}
```

#### GET `/oauth2/authorize`
OAuth2 authorization endpoint (redirects to login).

**Query Parameters:**
- `response_type=code` (required)
- `client_id` (required)
- `redirect_uri` (required)
- `scope` (optional)
- `state` (optional, CSRF protection)

**Example:**
```
GET /oauth2/authorize?response_type=code&client_id=product-service&redirect_uri=http://localhost:3000/callback&scope=read%20write&state=xyz123
```

#### POST `/oauth2/token`
OAuth2 token endpoint.

**Headers:**
```
Content-Type: application/x-www-form-urlencoded
Authorization: Basic <base64(client_id:client_secret)>
```

**Request (Authorization Code):**
```
grant_type=authorization_code&code=auth_code&redirect_uri=http://localhost:3000/callback
```

**Request (Client Credentials):**
```
grant_type=client_credentials&scope=user.read
```

**Request (Refresh Token):**
```
grant_type=refresh_token&refresh_token=refresh_token_value
```

**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "refresh_token_value",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "read write"
}
```

#### POST `/oauth2/introspect`
Token introspection endpoint.

**Headers:**
```
Content-Type: application/x-www-form-urlencoded
Authorization: Basic <base64(client_id:client_secret)>
```

**Request:**
```
token=access_token_value
```

**Response (200 OK):**
```json
{
  "active": true,
  "sub": "user123",
  "client_id": "product-service",
  "scope": "read write",
  "token_type": "Bearer",
  "exp": 1693526400,
  "iat": 1693522800,
  "authorities": ["ROLE_CUSTOMER"]
}
```

#### GET `/oauth2/jwks`
JWK Set endpoint for JWT signature verification.

**Response (200 OK):**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "use": "sig",
      "kid": "rsa-key-1",
      "n": "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtmUAmh9K8X1GYTA...",
      "e": "AQAB"
    }
  ]
}
```

### 4. Client Management Endpoints

#### POST `/clients/register`
Register a new OAuth2 client.

**Headers:**
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request:**
```json
{
  "clientId": "my-client",
  "clientName": "My Application",
  "redirectUris": [
    "http://localhost:3000/callback",
    "https://myapp.com/callback"
  ],
  "scopes": ["read", "write", "openid"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Client registered successfully",
  "data": {
    "clientId": "my-client",
    "clientSecret": "generated_secret_here",
    "clientName": "My Application",
    "redirectUris": [
      "http://localhost:3000/callback",
      "https://myapp.com/callback"
    ],
    "scopes": ["read", "write", "openid"]
  }
}
```

#### GET `/clients/{clientId}`
Get client information.

**Headers:**
```
Authorization: Bearer <admin_jwt_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "clientId": "my-client",
    "clientName": "My Application",
    "redirectUris": [
      "http://localhost:3000/callback"
    ],
    "scopes": ["read", "write", "openid"]
  }
}
```

#### POST `/clients/bootstrap/product-service`
Bootstrap the product service client (no auth required).

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "clientId": "product-service",
    "clientSecret": "product-service-secret",
    "clientName": "Product Service",
    "scopes": ["user.read", "user.validate", "read", "write", "openid", "profile"],
    "redirectUris": ["http://localhost:3000/callback"]
  }
}
```

---

## 📊 Data Schemas

### User Schema

```json
{
  "type": "object",
  "properties": {
    "userId": {
      "type": "integer",
      "format": "int64",
      "description": "Unique user identifier"
    },
    "username": {
      "type": "string",
      "minLength": 3,
      "maxLength": 50,
      "description": "Unique username"
    },
    "email": {
      "type": "string",
      "format": "email",
      "description": "User email address"
    },
    "password": {
      "type": "string",
      "minLength": 8,
      "description": "User password (write-only)"
    },
    "firstName": {
      "type": "string",
      "maxLength": 50,
      "description": "User first name"
    },
    "lastName": {
      "type": "string",
      "maxLength": 50,
      "description": "User last name"
    },
    "phone": {
      "type": "string",
      "pattern": "^[+]?[0-9]{10,15}$",
      "description": "User phone number"
    },
    "role": {
      "type": "string",
      "enum": ["CUSTOMER", "MERCHANT", "ADMIN"],
      "description": "User role"
    },
    "emailVerified": {
      "type": "boolean",
      "description": "Email verification status"
    },
    "accountLocked": {
      "type": "boolean",
      "description": "Account lock status"
    },
    "createdAt": {
      "type": "string",
      "format": "date-time",
      "description": "Account creation timestamp"
    },
    "updatedAt": {
      "type": "string",
      "format": "date-time",
      "description": "Last update timestamp"
    },
    "lastLogin": {
      "type": "string",
      "format": "date-time",
      "description": "Last login timestamp"
    }
  },
  "required": ["username", "email", "password"]
}
```

### Address Schema

```json
{
  "type": "object",
  "properties": {
    "addressId": {
      "type": "integer",
      "format": "int64",
      "description": "Unique address identifier"
    },
    "street": {
      "type": "string",
      "maxLength": 255,
      "description": "Street address"
    },
    "city": {
      "type": "string",
      "maxLength": 100,
      "description": "City"
    },
    "state": {
      "type": "string",
      "maxLength": 100,
      "description": "State or province"
    },
    "zipCode": {
      "type": "string",
      "maxLength": 20,
      "description": "ZIP or postal code"
    },
    "country": {
      "type": "string",
      "maxLength": 100,
      "description": "Country"
    },
    "type": {
      "type": "string",
      "enum": ["HOME", "WORK", "BILLING", "SHIPPING"],
      "description": "Address type"
    }
  },
  "required": ["street", "city", "state", "zipCode", "country", "type"]
}
```

### Error Response Schema

```json
{
  "type": "object",
  "properties": {
    "success": {
      "type": "boolean",
      "description": "Operation success status",
      "example": false
    },
    "message": {
      "type": "string",
      "description": "Human-readable error message",
      "example": "Invalid credentials provided"
    },
    "error": {
      "type": "object",
      "properties": {
        "code": {
          "type": "string",
          "description": "Error code for programmatic handling",
          "example": "AUTH001"
        },
        "field": {
          "type": "string",
          "description": "Field that caused the error (if applicable)",
          "example": "password"
        }
      }
    },
    "timestamp": {
      "type": "string",
      "format": "date-time",
      "description": "Error timestamp",
      "example": "2025-01-01T10:00:00Z"
    },
    "path": {
      "type": "string",
      "description": "Request path that caused the error",
      "example": "/auth/login"
    }
  },
  "required": ["success", "message"]
}
```

---

## 🚨 Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `AUTH001` | 401 | Invalid credentials |
| `AUTH002` | 401 | Account locked |
| `AUTH003` | 401 | Token expired |
| `AUTH004` | 403 | Insufficient permissions |
| `USER001` | 404 | User not found |
| `USER002` | 409 | Username/email already exists |
| `USER003` | 400 | Invalid user data |
| `CLIENT001` | 404 | OAuth2 client not found |
| `CLIENT002` | 400 | Invalid client configuration |
| `VALID001` | 400 | Validation error |
| `VALID002` | 400 | Strong password required |
| `VALID003` | 400 | Invalid email format |

---

## 🔄 Rate Limiting

| Endpoint | Limit | Window |
|----------|-------|--------|
| `/auth/login` | 5 attempts | 15 minutes |
| `/auth/signUp` | 3 registrations | 1 hour |
| `/oauth2/token` | 100 requests | 1 minute |
| `/oauth2/introspect` | 100 requests | 1 minute |
| Other endpoints | 1000 requests | 1 minute |

---

## 📈 Response Time SLAs

| Endpoint Type | Target Response Time | Max Response Time |
|---------------|---------------------|-------------------|
| Authentication | < 500ms | < 2s |
| User Profile | < 200ms | < 1s |
| OAuth2 Token | < 300ms | < 1s |
| Database queries | < 100ms | < 500ms |

---

*This API specification provides comprehensive documentation for all User Service endpoints. Use this as a reference for integration and development.*</content>
<parameter name="filePath">/Users/admin/IdeaProjects/ecommerce/user-service/API_SPECIFICATION.md
