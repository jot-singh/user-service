package com.user.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.user.service.error.ErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized API response format for all endpoints.
 * Provides consistent structure for both success and error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    // Response metadata
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    
    // Success response data
    private T data;
    
    // Error response details
    private ErrorDetails error;
    
    /**
     * Creates a successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a successful response with custom message and data
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with error code and message
     */
    public static <T> ApiResponse<T> error(ErrorCodes errorCode, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("Operation failed")
                .error(ErrorDetails.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    /**
     * Creates an error response with custom message
     */
    public static <T> ApiResponse<T> error(ErrorCodes errorCode, String customMessage, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("Operation failed")
                .error(ErrorDetails.builder()
                        .code(errorCode.getCode())
                        .message(customMessage)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    /**
     * Creates an error response with validation errors
     */
    public static <T> ApiResponse<T> validationError(List<String> validationErrors, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message("Validation failed")
                .error(ErrorDetails.builder()
                        .code(ErrorCodes.VALIDATION_FAILED.getCode())
                        .message(ErrorCodes.VALIDATION_FAILED.getMessage())
                        .validationErrors(validationErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    /**
     * Error details nested class
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private List<String> validationErrors;
        private String debugMessage;
    }
}
