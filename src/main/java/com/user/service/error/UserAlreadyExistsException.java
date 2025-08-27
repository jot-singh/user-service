package com.user.service.error;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super(String.format("User already exists with %s: %s", field, value));
    }
    
    public UserAlreadyExistsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
