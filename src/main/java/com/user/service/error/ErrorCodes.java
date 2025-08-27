package com.user.service.error;

/**
 * Enumeration of standardized error codes for the User Service.
 * These codes provide consistent error identification across the application.
 */
public enum ErrorCodes {
    
    // Authentication Errors (1000-1999)
    INVALID_CREDENTIALS("1001", "Invalid username or password"),
    TOKEN_EXPIRED("1002", "Authentication token has expired"),
    TOKEN_INVALID("1003", "Invalid authentication token"),
    TOKEN_MISSING("1004", "Authentication token is required"),
    ACCESS_DENIED("1005", "Access denied - insufficient privileges"),
    SESSION_EXPIRED("1006", "User session has expired"),
    
    // User Management Errors (2000-2999)
    USER_ALREADY_EXISTS("2001", "User with this username or email already exists"),
    USER_NOT_FOUND("2002", "User not found"),
    EMAIL_ALREADY_EXISTS("2003", "Email address is already registered"),
    USERNAME_ALREADY_EXISTS("2004", "Username is already taken"),
    INVALID_USER_DATA("2005", "Invalid user data provided"),
    ACCOUNT_LOCKED("2006", "User account is locked"),
    ACCOUNT_NOT_VERIFIED("2007", "User account is not verified"),
    
    // Validation Errors (3000-3999)
    VALIDATION_FAILED("3001", "Input validation failed"),
    INVALID_EMAIL_FORMAT("3002", "Invalid email format"),
    INVALID_PASSWORD_FORMAT("3003", "Password does not meet requirements"),
    INVALID_PHONE_FORMAT("3004", "Invalid phone number format"),
    REQUIRED_FIELD_MISSING("3005", "Required field is missing"),
    INVALID_REQUEST_FORMAT("3006", "Invalid request format"),
    
    // System Errors (4000-4999)
    INTERNAL_SERVER_ERROR("4001", "Internal server error occurred"),
    DATABASE_ERROR("4002", "Database operation failed"),
    EXTERNAL_SERVICE_ERROR("4003", "External service unavailable"),
    CONFIGURATION_ERROR("4004", "System configuration error"),
    
    // OAuth2 Errors (5000-5999)
    OAUTH2_CLIENT_NOT_FOUND("5001", "OAuth2 client not found"),
    OAUTH2_INVALID_CLIENT("5002", "Invalid OAuth2 client"),
    OAUTH2_INVALID_GRANT("5003", "Invalid OAuth2 grant"),
    OAUTH2_INVALID_SCOPE("5004", "Invalid OAuth2 scope"),
    OAUTH2_UNSUPPORTED_GRANT_TYPE("5005", "Unsupported OAuth2 grant type"),
    
    // Business Logic Errors (6000-6999)
    OPERATION_NOT_ALLOWED("6001", "Operation not allowed"),
    RESOURCE_CONFLICT("6002", "Resource conflict detected"),
    RATE_LIMIT_EXCEEDED("6003", "Rate limit exceeded"),
    SERVICE_UNAVAILABLE("6004", "Service temporarily unavailable");
    
    private final String code;
    private final String message;
    
    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", code, message);
    }
}
