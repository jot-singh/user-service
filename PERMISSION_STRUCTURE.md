# User Service - Permission Structure

## Overview
The user-service now has a clean, consistent permission structure based on **ADMIN** and **CUSTOMER** roles. This replaces the previous complex permission evaluator system with simple, role-based access control.

## Permission Structure

### ADMIN Role Permissions
**Full System Access:**
- ✅ All user management operations (view, edit, delete any user)
- ✅ All client management operations (register, view, delete clients)
- ✅ All address management operations (view, edit, delete any user's addresses)
- ✅ Administrative endpoints under `/admin/**`
- ✅ Access to Swagger UI and API documentation

### CUSTOMER Role Permissions
**Self-Service Access:**
- ✅ View and manage their own profile (`/users/profile`)
- ✅ View and manage their own addresses (`/users/addresses`)
- ✅ Basic user operations (read/write their own data)
- ✅ Cannot access other users' data
- ✅ Cannot perform administrative operations

## API Endpoints and Permissions

### User Profile Endpoints
```
GET    /users/profile           - CUSTOMER, ADMIN (own profile)
GET    /users/{userId}/profile  - ADMIN only (any user's profile)
PUT    /users/profile           - CUSTOMER, ADMIN (own profile)
PUT    /users/{userId}/profile  - ADMIN only (any user's profile)
```

### Address Management Endpoints
```
GET    /users/addresses              - CUSTOMER, ADMIN (own addresses)
GET    /users/{userId}/addresses     - ADMIN only (any user's addresses)
POST   /users/addresses              - CUSTOMER, ADMIN (create own address)
POST   /users/{userId}/addresses     - ADMIN only (create address for any user)
PUT    /users/addresses/{addressId}  - CUSTOMER, ADMIN (update own address)
DELETE /users/addresses/{addressId}  - CUSTOMER, ADMIN (delete own address)
```

### Administrative Endpoints
```
GET    /admin/users              - ADMIN only (list all users)
GET    /admin/users?page=0&size=20 - ADMIN only (paginated user list)
```

### Client Management Endpoints
```
POST   /clients/register              - ADMIN only (register new client)
GET    /clients                      - ADMIN only (list all clients)
GET    /clients/{clientId}           - ADMIN only (get client details)
DELETE /clients/{clientId}           - ADMIN only (delete client)
POST   /clients/bootstrap/product-service - PUBLIC (initial setup)
```

### Authentication Endpoints
```
POST   /auth/login              - PUBLIC
POST   /auth/signUp             - PUBLIC
POST   /auth/register           - PUBLIC
POST   /auth/verify             - PUBLIC
POST   /auth/activate           - PUBLIC
POST   /auth/resend-verification - PUBLIC
POST   /auth/check-email        - PUBLIC
POST   /auth/check-username     - PUBLIC
```

## Key Changes Made

### 1. Simplified Permission Model
- **Before**: Complex `hasPermission(#userId, 'user', 'READ')` expressions
- **After**: Simple `hasRole('CUSTOMER')` or `hasRole('ADMIN')` checks

### 2. Consistent Security Annotations
- All controllers now use `@PreAuthorize("hasRole('ADMIN')")` or `@PreAuthorize("hasRole('CUSTOMER')")`
- Removed redundant class-level and method-level annotations
- Eliminated dependency on custom permission evaluator

### 3. Clear API Design
- **Self-service endpoints**: `/users/profile`, `/users/addresses` (for current user)
- **Admin endpoints**: `/users/{userId}/profile`, `/users/{userId}/addresses` (for any user)
- **Admin-only endpoints**: `/admin/**`, `/clients/**` (except bootstrap)

### 4. Bootstrap Security
- `/clients/bootstrap/product-service` remains public for initial system setup
- All other client management requires ADMIN role

## Migration Notes

### For Existing Code
- Replace `hasPermission()` expressions with `hasRole()` checks
- Update client code to use appropriate endpoints based on user role
- Admin operations now require explicit ADMIN role

### For Frontend Applications
- **Customer users**: Use `/users/profile` and `/users/addresses` endpoints
- **Admin users**: Use `/users/{userId}/...` and `/admin/...` endpoints
- Check user roles before displaying admin features

### For API Testing
- Use bootstrap endpoint for initial client setup
- Create admin users through registration process
- Test with appropriate roles for different operations

## Security Benefits

1. **Clarity**: Easy to understand who can access what
2. **Consistency**: Uniform permission checks across all endpoints
3. **Maintainability**: Simple to modify permissions by changing roles
4. **Auditability**: Clear separation between customer and admin operations
5. **Performance**: No complex permission evaluation logic

## Testing the New Structure

### Test as Customer User:
```bash
# These should work
GET /users/profile
GET /users/addresses
PUT /users/profile
POST /users/addresses

# These should fail (403 Forbidden)
GET /admin/users
GET /users/123/profile
POST /clients/register
```

### Test as Admin User:
```bash
# These should work
GET /users/profile
GET /users/addresses
GET /admin/users
GET /users/123/profile
POST /clients/register
DELETE /clients/client-123
```

This new structure provides clear, maintainable, and secure access control for your ecommerce platform.
