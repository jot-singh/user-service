package com.user.service.validation;

import com.user.service.util.ValidationUtil;
import com.user.service.validation.annotations.StrongPassword;
import com.user.service.validation.annotations.ValidUsername;
import com.user.service.validation.validators.StrongPasswordValidator;
import com.user.service.validation.validators.ValidUsernameValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for validation system
 */
@ExtendWith(MockitoExtension.class)
class ValidationTest {

    @Mock
    private ConstraintValidatorContext context;

    private StrongPasswordValidator strongPasswordValidator;
    private ValidUsernameValidator validUsernameValidator;

    @BeforeEach
    void setUp() {
        strongPasswordValidator = new StrongPasswordValidator();
        validUsernameValidator = new ValidUsernameValidator();
    }

    // Custom Validation Annotations Tests

    @Test
    void testStrongPasswordValidator_ValidPassword() {
        StrongPassword annotation = createStrongPasswordAnnotation(8, 128, true, true, true, true);
        strongPasswordValidator.initialize(annotation);
        
        assertTrue(strongPasswordValidator.isValid("StrongPass123!", context));
        assertTrue(strongPasswordValidator.isValid("MyP@ssw0rd", context));
    }

    @Test
    void testStrongPasswordValidator_InvalidPassword() {
        StrongPassword annotation = createStrongPasswordAnnotation(8, 128, true, true, true, true);
        strongPasswordValidator.initialize(annotation);
        
        assertFalse(strongPasswordValidator.isValid("weak", context)); // too short
        assertFalse(strongPasswordValidator.isValid("weakpassword", context)); // no uppercase, digit, special
        assertFalse(strongPasswordValidator.isValid("WEAKPASSWORD", context)); // no lowercase, digit, special
        assertFalse(strongPasswordValidator.isValid("WeakPassword", context)); // no digit, special
    }

    @Test
    void testValidUsernameValidator_ValidUsername() {
        ValidUsername annotation = createValidUsernameAnnotation(3, 50, false);
        validUsernameValidator.initialize(annotation);
        
        assertTrue(validUsernameValidator.isValid("john_doe", context));
        assertTrue(validUsernameValidator.isValid("user123", context));
        assertTrue(validUsernameValidator.isValid("test_user", context));
    }

    @Test
    void testValidUsernameValidator_InvalidUsername() {
        ValidUsername annotation = createValidUsernameAnnotation(3, 50, false);
        validUsernameValidator.initialize(annotation);
        
        assertFalse(validUsernameValidator.isValid("ab", context)); // too short
        assertFalse(validUsernameValidator.isValid("user-name", context)); // contains hyphen
        assertFalse(validUsernameValidator.isValid("user.name", context)); // contains dot
        assertFalse(validUsernameValidator.isValid("admin", context)); // reserved name
    }

    @Test
    void testValidUsernameValidator_ReservedNamesAllowed() {
        ValidUsername annotation = createValidUsernameAnnotation(3, 50, true);
        validUsernameValidator.initialize(annotation);
        
        assertTrue(validUsernameValidator.isValid("admin", context)); // reserved name allowed
        assertTrue(validUsernameValidator.isValid("test", context)); // reserved name allowed
    }

    // Validation Utility Tests

    @Test
    void testValidationUtil_validateNotBlank() {
        assertDoesNotThrow(() -> ValidationUtil.validateNotBlank("valid", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateNotBlank("", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateNotBlank("   ", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateNotBlank(null, "field"));
    }

    @Test
    void testValidationUtil_validateStringLength() {
        assertDoesNotThrow(() -> ValidationUtil.validateStringLength("test", "field", 1, 10));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateStringLength("test", "field", 10, 20)); // too short
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateStringLength("verylongstring", "field", 1, 10)); // too long
    }

    @Test
    void testValidationUtil_validateEmail() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("test@example.com", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("user+tag@domain.co.uk", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateEmail("invalid-email", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateEmail("test@", "field"));
    }

    @Test
    void testValidationUtil_validateUsername() {
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("valid_user", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validateUsername("user123", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUsername("user-name", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUsername("user.name", "field"));
    }

    @Test
    void testValidationUtil_validatePassword() {
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("StrongPass123!", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validatePassword("MyP@ssw0rd", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validatePassword("weak", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validatePassword("weakpassword", "field"));
    }

    @Test
    void testValidationUtil_validatePhoneNumber() {
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("+1-555-123-4567", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("(555) 123-4567", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validatePhoneNumber("555.123.4567", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validatePhoneNumber("abc-def-ghij", "field"));
    }

    @Test
    void testValidationUtil_validateUrl() {
        assertDoesNotThrow(() -> ValidationUtil.validateUrl("https://example.com", "field"));
        assertDoesNotThrow(() -> ValidationUtil.validateUrl("http://sub.domain.co.uk/path?param=value", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUrl("not-a-url", "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUrl("ftp://example.com", "field")); // only http/https allowed
    }

    @Test
    void testValidationUtil_validateEnumValue() {
        String[] allowedRoles = {"USER", "ADMIN", "MODERATOR"};
        
        assertDoesNotThrow(() -> ValidationUtil.validateEnumValue("USER", allowedRoles, "field"));
        assertDoesNotThrow(() -> ValidationUtil.validateEnumValue("ADMIN", allowedRoles, "field"));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateEnumValue("GUEST", allowedRoles, "field"));
    }

    @Test
    void testValidationUtil_validateNumericRange() {
        assertDoesNotThrow(() -> ValidationUtil.validateNumericRange(5, "field", 1, 10));
        assertDoesNotThrow(() -> ValidationUtil.validateNumericRange(1, "field", 1, 10));
        assertDoesNotThrow(() -> ValidationUtil.validateNumericRange(10, "field", 1, 10));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateNumericRange(0, "field", 1, 10));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateNumericRange(11, "field", 1, 10));
    }

    @Test
    void testValidationUtil_validateList() {
        List<String> validList = Arrays.asList("item1", "item2", "item3");
        List<String> largeList = Arrays.asList("item1", "item2", "item3", "item4", "item5");
        
        assertDoesNotThrow(() -> ValidationUtil.validateList(validList, "field", 5));
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateList(largeList, "field", 3));
    }

    @Test
    void testValidationUtil_validateUserBusinessRules() {
        // Valid cases
        assertDoesNotThrow(() -> ValidationUtil.validateUserBusinessRules("john_doe", "john@example.com", "USER"));
        assertDoesNotThrow(() -> ValidationUtil.validateUserBusinessRules("admin", "admin@example.com", "ADMIN"));
        
        // Invalid cases
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUserBusinessRules("admin", "john@example.com", "USER")); // reserved username
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUserBusinessRules("john_doe", "test@test.com", "USER")); // test domain
        assertThrows(com.user.service.error.ValidationException.class, 
            () -> ValidationUtil.validateUserBusinessRules("john_doe", "john@example.com", "ADMIN")); // non-admin creating admin
    }

    // Helper methods to create annotation instances for testing

    private StrongPassword createStrongPasswordAnnotation(int minLength, int maxLength, 
                                                       boolean requireUppercase, boolean requireLowercase,
                                                       boolean requireDigit, boolean requireSpecialChar) {
        return new StrongPassword() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return StrongPassword.class;
            }

            @Override
            public String message() {
                return "Password must be strong";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int minLength() {
                return minLength;
            }

            @Override
            public int maxLength() {
                return maxLength;
            }

            @Override
            public boolean requireUppercase() {
                return requireUppercase;
            }

            @Override
            public boolean requireLowercase() {
                return requireLowercase;
            }

            @Override
            public boolean requireDigit() {
                return requireDigit;
            }

            @Override
            public boolean requireSpecialChar() {
                return requireSpecialChar;
            }
        };
    }

    private ValidUsername createValidUsernameAnnotation(int minLength, int maxLength, boolean allowReservedNames) {
        return new ValidUsername() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ValidUsername.class;
            }

            @Override
            public String message() {
                return "Username must be valid";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public int minLength() {
                return minLength;
            }

            @Override
            public int maxLength() {
                return maxLength;
            }

            @Override
            public boolean allowReservedNames() {
                return allowReservedNames;
            }
        };
    }
}
