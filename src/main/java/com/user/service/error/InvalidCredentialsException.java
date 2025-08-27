package com.user.service.error;

/**
 * Exception thrown when user provides invalid login credentials.
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }
    
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
