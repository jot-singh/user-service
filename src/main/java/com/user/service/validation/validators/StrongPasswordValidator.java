package com.user.service.validation.validators;

import com.user.service.validation.annotations.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator implementation for StrongPassword annotation
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    
    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    
    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // null values are handled by @NotNull if needed
        }
        
        // Check length constraints
        if (password.length() < minLength || password.length() > maxLength) {
            return false;
        }
        
        // Check character requirements
        if (requireUppercase && !Pattern.compile("[A-Z]").matcher(password).find()) {
            return false;
        }
        
        if (requireLowercase && !Pattern.compile("[a-z]").matcher(password).find()) {
            return false;
        }
        
        if (requireDigit && !Pattern.compile("\\d").matcher(password).find()) {
            return false;
        }
        
        if (requireSpecialChar && !Pattern.compile("[@$!%*?&]").matcher(password).find()) {
            return false;
        }
        
        return true;
    }
}
