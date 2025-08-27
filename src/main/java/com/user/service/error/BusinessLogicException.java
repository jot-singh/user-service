package com.user.service.error;

/**
 * Exception thrown when a business operation is not allowed due to business rules.
 */
public class BusinessLogicException extends RuntimeException {
    
    private final ErrorCodes errorCode;
    
    public BusinessLogicException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BusinessLogicException(ErrorCodes errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public BusinessLogicException(ErrorCodes errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCodes getErrorCode() {
        return errorCode;
    }
}
