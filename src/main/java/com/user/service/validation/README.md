# User Service Validation System

## Overview

This document describes the comprehensive validation system implemented for the User Service, providing input validation, custom validation annotations, and validation utilities.

## 🏗️ Architecture

The validation system consists of several layers:

1. **Bean Validation Annotations** - Standard Jakarta validation annotations
2. **Custom Validation Annotations** - Domain-specific validation rules
3. **Validation Utilities** - Common validation logic and business rules
4. **Global Exception Handler** - Centralized validation error handling
5. **DTOs with Validation** - Request/Response objects with validation constraints

## 📋 Standard Validation Annotations

### Core Annotations

- `@NotBlank` - Ensures string is not null, empty, or only whitespace
- `@Size` - Validates string length constraints
- `@Email` - Validates email format
- `@Pattern` - Validates against regex patterns
- `@Min/@Max` - Validates numeric ranges
- `@NotNull` - Ensures field is not null

### Usage Examples

```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
private String username;

@Email(message = "Email must be a valid email address")
@Size(max = 100, message = "Email must be less than 100 characters")
private String email;
```

## 🎯 Custom Validation Annotations

### StrongPassword

Validates password strength requirements with configurable constraints.

```java
@StrongPassword(
    minLength = 8,
    maxLength = 128,
    requireUppercase = true,
    requireLowercase = true,
    requireDigit = true,
    requireSpecialChar = true
)
private String password;
```

**Requirements:**
- Minimum length: 8 characters
- Maximum length: 128 characters
- Must contain uppercase letter
- Must contain lowercase letter
- Must contain digit
- Must contain special character (@$!%*?&)

### ValidUsername

Validates username format and prevents reserved names.

```java
@ValidUsername(
    minLength = 3,
    maxLength = 50,
    allowReservedNames = false
)
private String username;
```

**Requirements:**
- Only letters, numbers, and underscores allowed
- Configurable length constraints
- Optional reserved name protection (admin, root, system, etc.)

## 🛠️ Validation Utilities

### ValidationUtil Class

Provides common validation methods for business logic validation:

```java
// String validation
ValidationUtil.validateNotBlank(value, "fieldName");
ValidationUtil.validateStringLength(value, "fieldName", min, max);
ValidationUtil.validateEmail(email, "fieldName");
ValidationUtil.validateUsername(username, "fieldName");
ValidationUtil.validatePassword(password, "fieldName");

// Business rule validation
ValidationUtil.validateUserBusinessRules(username, email, role);
ValidationUtil.validateEnumValue(value, allowedValues, "fieldName");
ValidationUtil.validateNumericRange(value, "fieldName", min, max);
```

### Business Rule Validation

The system includes business rule validation for:

- **Username restrictions**: Prevents reserved names like 'admin'
- **Email domain restrictions**: Blocks test domains in production
- **Role restrictions**: Controls who can create admin accounts
- **Password strength**: Enforces security requirements

## 📊 DTOs with Validation

### Request DTOs

All request DTOs include comprehensive validation:

- **AuthRequestDto**: Login/signup requests
- **UserUpdateRequestDto**: User profile updates
- **PasswordChangeRequestDto**: Password changes
- **ClientRegistrationDto**: OAuth2 client registration
- **UserSearchRequestDto**: User search with pagination

### Response DTOs

Response DTOs include validation for data integrity:

- **UserResponseDto**: User information responses
- **ClientResponseDto**: OAuth2 client responses
- **PaginatedResponseDto**: Paginated response wrapper

## 🚨 Error Handling

### Validation Exception Types

- `ValidationException`: Custom validation errors
- `MethodArgumentNotValidException`: Bean validation failures
- `MissingServletRequestParameterException`: Missing parameters
- `MethodArgumentTypeMismatchException`: Type conversion errors

### Error Response Format

```json
{
  "success": false,
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/auth/signup",
  "error": {
    "code": "3001",
    "message": "Validation failed",
    "validationErrors": [
      "username: Username can only contain letters, numbers, and underscores",
      "password: Password must contain at least one uppercase letter"
    ]
  }
}
```

## 🧪 Testing

### Test Coverage

The validation system includes comprehensive tests for:

- Custom validation annotations
- Validation utility methods
- Business rule validation
- Error scenarios
- Edge cases

### Running Tests

```bash
# Run all validation tests
mvn test -Dtest=ValidationTest

# Run specific test methods
mvn test -Dtest=ValidationTest#testStrongPasswordValidator_ValidPassword
```

## 🔧 Configuration

### Validation Groups

Support for validation groups allows different validation rules for different scenarios:

```java
public interface CreateUser {}
public interface UpdateUser {}

@ValidUsername(groups = {CreateUser.class, UpdateUser.class})
@StrongPassword(groups = CreateUser.class) // Only validate on creation
private String password;
```

### Custom Error Messages

Error messages can be customized through:

- Annotation message attributes
- Message properties files
- Custom validation logic

## 📈 Performance Considerations

### Validation Optimization

- **Lazy validation**: Validation only occurs when needed
- **Early exit**: Validation stops on first failure
- **Cached patterns**: Regex patterns are compiled once
- **Minimal overhead**: Validation adds minimal runtime cost

### Best Practices

1. **Validate early**: Validate input as soon as possible
2. **Fail fast**: Stop validation on first error
3. **Clear messages**: Provide helpful error messages
4. **Consistent format**: Use consistent validation patterns
5. **Test thoroughly**: Test all validation scenarios

## 🔒 Security Considerations

### Input Sanitization

- **Pattern validation**: Restrict input to safe patterns
- **Length limits**: Prevent buffer overflow attacks
- **Reserved name protection**: Prevent impersonation attacks
- **Business rule enforcement**: Enforce security policies

### Validation Order

1. **Format validation**: Check input format first
2. **Length validation**: Verify size constraints
3. **Pattern validation**: Ensure safe character sets
4. **Business validation**: Apply domain-specific rules

## 🚀 Future Enhancements

### Planned Features

- **Async validation**: Background validation for complex rules
- **Validation caching**: Cache validation results
- **Dynamic rules**: Runtime validation rule configuration
- **Internationalization**: Multi-language error messages
- **Validation metrics**: Performance and usage tracking

### Extension Points

The validation system is designed for easy extension:

- Add new custom annotations
- Implement new validators
- Extend business rule validation
- Customize error handling
- Add validation hooks

## 📚 References

- [Jakarta Bean Validation Specification](https://beanvalidation.org/)
- [Spring Validation Framework](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation)
- [Hibernate Validator](https://hibernate.org/validator/)
- [Java Regular Expressions](https://docs.oracle.com/javase/tutorial/essential/regex/)

## 🤝 Contributing

When adding new validation rules:

1. **Follow patterns**: Use existing validation patterns
2. **Add tests**: Include comprehensive test coverage
3. **Document**: Update this README
4. **Review**: Ensure security and performance considerations
5. **Validate**: Test with real-world scenarios
