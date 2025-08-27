package com.user.service.error;

import com.user.service.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler that provides standardized error responses
 * for all exceptions thrown across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Authentication & Authorization Exceptions
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentialsException(
            InvalidCredentialsException e, WebRequest request) {
        logger.warn("Invalid credentials attempt: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCodes.INVALID_CREDENTIALS, getPath(request)));
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExistsException(
            UserAlreadyExistsException e, WebRequest request) {
        logger.warn("User already exists: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ErrorCodes.USER_ALREADY_EXISTS, e.getMessage(), getPath(request)));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(
            UserNotFoundException e, WebRequest request) {
        logger.warn("User not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCodes.USER_NOT_FOUND, e.getMessage(), getPath(request)));
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountLockedException(
            AccountLockedException e, WebRequest request) {
        logger.warn("Account locked: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCodes.ACCOUNT_LOCKED, e.getMessage(), getPath(request)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException e, WebRequest request) {
        logger.warn("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCodes.ACCESS_DENIED, getPath(request)));
    }

    // JWT Token Exceptions
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtSignatureException(
            SignatureException e, WebRequest request) {
        logger.warn("Invalid JWT signature: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCodes.TOKEN_INVALID, "Invalid JWT signature", getPath(request)));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(
            ExpiredJwtException e, WebRequest request) {
        logger.warn("JWT token expired: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCodes.TOKEN_EXPIRED, getPath(request)));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(
            JwtException e, WebRequest request) {
        logger.warn("JWT token error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCodes.TOKEN_INVALID, e.getMessage(), getPath(request)));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(
            UsernameNotFoundException e, WebRequest request) {
        logger.warn("Username not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCodes.USER_NOT_FOUND, e.getMessage(), getPath(request)));
    }

    // Validation Exceptions
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            ValidationException e, WebRequest request) {
        logger.warn("Validation error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(e.getValidationErrors(), getPath(request)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request) {
        logger.warn("Method argument validation failed: {}", e.getMessage());
        
        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>();
        
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.add(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError(errors, getPath(request)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, WebRequest request) {
        logger.warn("Missing request parameter: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodes.REQUIRED_FIELD_MISSING, 
                        String.format("Required parameter '%s' is missing", e.getParameterName()), 
                        getPath(request)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, WebRequest request) {
        logger.warn("Method argument type mismatch: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodes.INVALID_REQUEST_FORMAT, 
                        String.format("Invalid value '%s' for parameter '%s'", e.getValue(), e.getName()), 
                        getPath(request)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        logger.warn("HTTP message not readable: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodes.INVALID_REQUEST_FORMAT, 
                        "Invalid request format or malformed JSON", getPath(request)));
    }

    // Business Logic Exceptions
    
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessLogicException(
            BusinessLogicException e, WebRequest request) {
        logger.warn("Business logic error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage(), getPath(request)));
    }

    // HTTP Method Exceptions
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, WebRequest request) {
        logger.warn("HTTP method not supported: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(ErrorCodes.OPERATION_NOT_ALLOWED, 
                        String.format("Method '%s' not supported", e.getMethod()), 
                        getPath(request)));
    }

    // Database Exceptions
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(
            DataAccessException e, WebRequest request) {
        logger.error("Database error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodes.DATABASE_ERROR, getPath(request)));
    }

    // Generic Exception Handler
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception e, WebRequest request) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodes.INTERNAL_SERVER_ERROR, getPath(request)));
    }

    // Utility Methods
    
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
