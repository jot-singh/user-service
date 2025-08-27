package com.user.service.error;

import java.util.List;

/**
 * Exception thrown when request validation fails.
 */
public class ValidationException extends RuntimeException {
    
    private final List<String> validationErrors;
    
    public ValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }
    
    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(List<String> validationErrors) {
        super("Validation failed");
        this.validationErrors = validationErrors;
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
