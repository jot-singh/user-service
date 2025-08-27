package com.user.service.util;

import com.user.service.error.ValidationException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
public class ValidationUtil {
    
    // Common regex patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()\\.]+$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://[\\w\\d\\-._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$");
    
    /**
     * Validates that a string is not null, empty, or only whitespace
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ValidationException(List.of(fieldName + " cannot be blank"));
        }
    }
    
    /**
     * Validates string length constraints
     */
    public static void validateStringLength(String value, String fieldName, int minLength, int maxLength) {
        if (value != null) {
            if (value.length() < minLength) {
                throw new ValidationException(List.of(fieldName + " must be at least " + minLength + " characters"));
            }
            if (value.length() > maxLength) {
                throw new ValidationException(List.of(fieldName + " must be less than " + maxLength + " characters"));
            }
        }
    }
    
    /**
     * Validates email format
     */
    public static void validateEmail(String email, String fieldName) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(List.of(fieldName + " must be a valid email address"));
        }
    }
    
    /**
     * Validates username format
     */
    public static void validateUsername(String username, String fieldName) {
        if (username != null && !USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException(List.of(fieldName + " can only contain letters, numbers, and underscores"));
        }
    }
    
    /**
     * Validates password strength
     */
    public static void validatePassword(String password, String fieldName) {
        if (password != null && !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException(List.of(fieldName + " must contain at least one uppercase letter, one lowercase letter, one number, and one special character"));
        }
    }
    
    /**
     * Validates phone number format
     */
    public static void validatePhoneNumber(String phoneNumber, String fieldName) {
        if (phoneNumber != null && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException(List.of(fieldName + " can only contain digits, spaces, hyphens, parentheses, and plus sign"));
        }
    }
    
    /**
     * Validates URL format
     */
    public static void validateUrl(String url, String fieldName) {
        if (url != null && !URL_PATTERN.matcher(url).matches()) {
            throw new ValidationException(List.of(fieldName + " must be a valid HTTP/HTTPS URL"));
        }
    }
    
    /**
     * Validates that a value is one of the allowed values
     */
    public static <T> void validateEnumValue(T value, T[] allowedValues, String fieldName) {
        if (value != null) {
            boolean isValid = false;
            for (T allowedValue : allowedValues) {
                if (value.equals(allowedValue)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                throw new ValidationException(List.of(fieldName + " must be one of: " + String.join(", ", 
                    java.util.Arrays.stream(allowedValues).map(Object::toString).toList())));
            }
        }
    }
    
    /**
     * Validates numeric range constraints
     */
    public static void validateNumericRange(Number value, String fieldName, Number min, Number max) {
        if (value != null) {
            double doubleValue = value.doubleValue();
            if (min != null && doubleValue < min.doubleValue()) {
                throw new ValidationException(List.of(fieldName + " must be at least " + min));
            }
            if (max != null && doubleValue > max.doubleValue()) {
                throw new ValidationException(List.of(fieldName + " cannot exceed " + max));
            }
        }
    }
    
    /**
     * Validates that a list is not null and has valid size
     */
    public static <T> void validateList(List<T> list, String fieldName, int maxSize) {
        if (list != null && list.size() > maxSize) {
            throw new ValidationException(List.of(fieldName + " cannot contain more than " + maxSize + " items"));
        }
    }
    
    /**
     * Validates business rules for user operations
     */
    public static void validateUserBusinessRules(String username, String email, String role) {
        List<String> errors = new ArrayList<>();
        
        // Username cannot be 'admin' for security reasons (unless it's the actual admin user)
        if ("admin".equalsIgnoreCase(username) && !"ADMIN".equals(role)) {
            errors.add("Username 'admin' is reserved and cannot be used");
        }
        
        // Email domain restrictions (example)
        if (email != null && email.toLowerCase().contains("@test.com")) {
            errors.add("Test email domains are not allowed for production use");
        }
        
        // Role restrictions - only allow admin role if username is 'admin'
        if ("ADMIN".equals(role) && !"admin".equals(username)) {
            errors.add("Only admin users can create admin accounts");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
