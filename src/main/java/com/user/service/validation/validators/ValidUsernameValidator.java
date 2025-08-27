package com.user.service.validation.validators;

import com.user.service.validation.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator implementation for ValidUsername annotation
 */
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    
    private int minLength;
    private int maxLength;
    private boolean allowReservedNames;
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final List<String> RESERVED_NAMES = Arrays.asList(
        "admin", "administrator", "root", "system", "guest", "anonymous", "test", "demo"
    );
    
    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.allowReservedNames = constraintAnnotation.allowReservedNames();
    }
    
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // null values are handled by @NotNull if needed
        }
        
        // Check length constraints
        if (username.length() < minLength || username.length() > maxLength) {
            return false;
        }
        
        // Check pattern (only letters, numbers, and underscores)
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }
        
        // Check for reserved names if not allowed
        if (!allowReservedNames && RESERVED_NAMES.contains(username.toLowerCase())) {
            return false;
        }
        
        return true;
    }
}
