package com.user.service.error;

/**
 * Exception thrown when an account is locked or suspended.
 */
public class AccountLockedException extends RuntimeException {
    
    public AccountLockedException(String message) {
        super(message);
    }
    
    public AccountLockedException(String username, String reason) {
        super(String.format("Account locked for user '%s': %s", username, reason));
    }
    
    public AccountLockedException() {
        super("Account is locked");
    }
}
