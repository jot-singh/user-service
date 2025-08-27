package com.user.service.error;

import com.user.service.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify the error handling infrastructure.
 */
@SpringBootTest
public class ErrorHandlingTest {

    @Test
    public void testErrorCodesEnum() {
        // Test that error codes have proper structure
        ErrorCodes errorCode = ErrorCodes.INVALID_CREDENTIALS;
        
        assertNotNull(errorCode.getCode());
        assertNotNull(errorCode.getMessage());
        assertEquals("1001", errorCode.getCode());
        assertTrue(errorCode.getMessage().contains("Invalid username or password"));
    }

    @Test
    public void testApiResponseSuccessCreation() {
        // Test successful response creation
        String testData = "Test Data";
        ApiResponse<String> response = ApiResponse.success(testData);
        
        assertTrue(response.isSuccess());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
        assertEquals("Operation completed successfully", response.getMessage());
        assertNull(response.getError());
    }

    @Test
    public void testApiResponseErrorCreation() {
        // Test error response creation
        String testPath = "/test/path";
        ApiResponse<Object> response = ApiResponse.error(ErrorCodes.USER_NOT_FOUND, testPath);
        
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getError());
        assertEquals(ErrorCodes.USER_NOT_FOUND.getCode(), response.getError().getCode());
        assertEquals(ErrorCodes.USER_NOT_FOUND.getMessage(), response.getError().getMessage());
        assertEquals(testPath, response.getPath());
    }

    @Test
    public void testApiResponseValidationError() {
        // Test validation error response creation
        java.util.List<String> validationErrors = java.util.List.of(
            "Username is required",
            "Email format is invalid"
        );
        String testPath = "/auth/signup";
        
        ApiResponse<Object> response = ApiResponse.validationError(validationErrors, testPath);
        
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertNotNull(response.getError());
        assertEquals(ErrorCodes.VALIDATION_FAILED.getCode(), response.getError().getCode());
        assertEquals(validationErrors, response.getError().getValidationErrors());
        assertEquals(testPath, response.getPath());
    }

    @Test
    public void testCustomExceptions() {
        // Test custom exception creation
        String username = "testuser";
        
        InvalidCredentialsException invalidCredentials = new InvalidCredentialsException();
        assertNotNull(invalidCredentials.getMessage());
        
        UserNotFoundException userNotFound = new UserNotFoundException("email", "test@example.com");
        assertTrue(userNotFound.getMessage().contains("test@example.com"));
        
        UserAlreadyExistsException userExists = new UserAlreadyExistsException("username", username);
        assertTrue(userExists.getMessage().contains(username));
        
        ValidationException validation = new ValidationException("Invalid input");
        assertEquals(1, validation.getValidationErrors().size());
        assertEquals("Invalid input", validation.getValidationErrors().get(0));
        
        AccountLockedException accountLocked = new AccountLockedException();
        assertNotNull(accountLocked.getMessage());
        
        BusinessLogicException businessLogic = new BusinessLogicException(ErrorCodes.OPERATION_NOT_ALLOWED);
        assertEquals(ErrorCodes.OPERATION_NOT_ALLOWED, businessLogic.getErrorCode());
    }
}
