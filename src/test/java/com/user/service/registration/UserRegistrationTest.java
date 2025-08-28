package com.user.service.registration;

import com.user.service.dto.request.UserRegistrationRequestDto;
import com.user.service.dto.response.UserRegistrationResponseDto;
import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.entity.VerificationToken;
import com.user.service.repository.VerificationTokenRepository;
import com.user.service.services.EmailService;
import com.user.service.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for User Registration System
 * Tests registration, email verification, and account activation
 */
@ExtendWith(MockitoExtension.class)
class UserRegistrationTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRegistrationRequestDto validRegistrationRequest;
    private User testUser;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        // Setup valid registration request
        validRegistrationRequest = UserRegistrationRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePass123!")
                .confirmPassword("SecurePass123!")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .acceptTerms(true)
                .acceptPrivacyPolicy(true)
                .sendWelcomeEmail(true)
                .requireEmailVerification(true)
                .build();

        // Setup test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .emailVerified(false)
                .accountLocked(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup test verification token
        testToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testValidUserRegistration() {
        // Given
        when(registrationService.registerUser(any(UserRegistrationRequestDto.class)))
                .thenReturn(UserRegistrationResponseDto.success(
                    "1", "testuser", "test@example.com", "Test", "User", Role.CUSTOMER
                ));

        // When
        UserRegistrationResponseDto response = registrationService.registerUser(validRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertFalse(response.getEmailVerified());
        assertFalse(response.getAccountActive());
        assertTrue(response.getRequiresEmailVerification());
        assertTrue(response.getVerificationEmailSent());
        assertEquals("test@example.com", response.getVerificationEmailSentTo());

        verify(registrationService).registerUser(validRegistrationRequest);
    }

    @Test
    void testUserRegistrationWithExistingEmail() {
        // Given
        when(registrationService.registerUser(any(UserRegistrationRequestDto.class)))
                .thenReturn(UserRegistrationResponseDto.userAlreadyExists("test@example.com"));

        // When
        UserRegistrationResponseDto response = registrationService.registerUser(validRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("USER_EXISTS", response.getStatus());
        assertEquals("test@example.com", response.getEmail());
        assertFalse(response.getRequiresEmailVerification());
        assertFalse(response.getVerificationEmailSent());
        assertEquals("A user with this email already exists.", response.getMessage());
        assertEquals("Try logging in or use the forgot password feature.", response.getNextSteps());
    }

    @Test
    void testUserRegistrationWithExistingUsername() {
        // Given
        when(registrationService.registerUser(any(UserRegistrationRequestDto.class)))
                .thenReturn(UserRegistrationResponseDto.validationError("Username is already taken"));

        // When
        UserRegistrationResponseDto response = registrationService.registerUser(validRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("VALIDATION_ERROR", response.getStatus());
        assertEquals("Username is already taken", response.getMessage());
        assertFalse(response.getRequiresEmailVerification());
        assertFalse(response.getVerificationEmailSent());
        assertEquals("Please correct the errors and try again.", response.getNextSteps());
    }

    @Test
    void testUserRegistrationSystemError() {
        // Given
        when(registrationService.registerUser(any(UserRegistrationRequestDto.class)))
                .thenReturn(UserRegistrationResponseDto.systemError("Registration failed due to system error"));

        // When
        UserRegistrationResponseDto response = registrationService.registerUser(validRegistrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("SYSTEM_ERROR", response.getStatus());
        assertEquals("Registration failed due to system error", response.getMessage());
        assertFalse(response.getRequiresEmailVerification());
        assertFalse(response.getVerificationEmailSent());
        assertEquals("Please try again later or contact support.", response.getNextSteps());
    }

    @Test
    void testEmailVerificationSuccess() {
        // Given
        when(registrationService.verifyEmail(anyString())).thenReturn(true);

        // When
        boolean result = registrationService.verifyEmail("valid-token");

        // Then
        assertTrue(result);
        verify(registrationService).verifyEmail("valid-token");
    }

    @Test
    void testEmailVerificationFailure() {
        // Given
        when(registrationService.verifyEmail(anyString())).thenReturn(false);

        // When
        boolean result = registrationService.verifyEmail("invalid-token");

        // Then
        assertFalse(result);
        verify(registrationService).verifyEmail("invalid-token");
    }

    @Test
    void testAccountActivationSuccess() {
        // Given
        when(registrationService.activateAccount(anyString())).thenReturn(true);

        // When
        boolean result = registrationService.activateAccount("valid-activation-token");

        // Then
        assertTrue(result);
        verify(registrationService).activateAccount("valid-activation-token");
    }

    @Test
    void testAccountActivationFailure() {
        // Given
        when(registrationService.activateAccount(anyString())).thenReturn(false);

        // When
        boolean result = registrationService.activateAccount("invalid-activation-token");

        // Then
        assertFalse(result);
        verify(registrationService).activateAccount("invalid-activation-token");
    }

    @Test
    void testResendVerificationEmailSuccess() {
        // Given
        when(registrationService.resendVerificationEmail(anyString())).thenReturn(true);

        // When
        boolean result = registrationService.resendVerificationEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(registrationService).resendVerificationEmail("test@example.com");
    }

    @Test
    void testResendVerificationEmailFailure() {
        // Given
        when(registrationService.resendVerificationEmail(anyString())).thenReturn(false);

        // When
        boolean result = registrationService.resendVerificationEmail("test@example.com");

        // Then
        assertFalse(result);
        verify(registrationService).resendVerificationEmail("test@example.com");
    }

    @Test
    void testEmailAvailabilityCheck() {
        // Given
        when(registrationService.isEmailAlreadyRegistered("test@example.com")).thenReturn(false);
        when(registrationService.isEmailAlreadyRegistered("existing@example.com")).thenReturn(true);

        // When & Then
        assertFalse(registrationService.isEmailAlreadyRegistered("test@example.com"));
        assertTrue(registrationService.isEmailAlreadyRegistered("existing@example.com"));

        verify(registrationService).isEmailAlreadyRegistered("test@example.com");
        verify(registrationService).isEmailAlreadyRegistered("existing@example.com");
    }

    @Test
    void testUsernameAvailabilityCheck() {
        // Given
        when(registrationService.isUsernameAlreadyTaken("testuser")).thenReturn(false);
        when(registrationService.isUsernameAlreadyTaken("existinguser")).thenReturn(true);

        // When & Then
        assertFalse(registrationService.isUsernameAlreadyTaken("testuser"));
        assertTrue(registrationService.isUsernameAlreadyTaken("existinguser"));

        verify(registrationService).isUsernameAlreadyTaken("testuser");
        verify(registrationService).isUsernameAlreadyTaken("existinguser");
    }

    @Test
    void testGenerateNewVerificationTokenSuccess() {
        // Given
        when(registrationService.generateNewVerificationToken(anyString())).thenReturn(true);

        // When
        boolean result = registrationService.generateNewVerificationToken("test@example.com");

        // Then
        assertTrue(result);
        verify(registrationService).generateNewVerificationToken("test@example.com");
    }

    @Test
    void testGenerateNewVerificationTokenFailure() {
        // Given
        when(registrationService.generateNewVerificationToken(anyString())).thenReturn(false);

        // When
        boolean result = registrationService.generateNewVerificationToken("test@example.com");

        // Then
        assertFalse(result);
        verify(registrationService).generateNewVerificationToken("test@example.com");
    }

    @Test
    void testVerificationTokenValidation() {
        // Given
        String validToken = UUID.randomUUID().toString();
        VerificationToken token = VerificationToken.builder()
                .token(validToken)
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .build();

        // When & Then
        assertTrue(token.isValid());
        assertFalse(token.isExpired());
        assertFalse(token.getUsed());

        // Test expired token
        VerificationToken expiredToken = VerificationToken.builder()
                .token("expired-token")
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().minusHours(1))
                .used(false)
                .build();

        assertFalse(expiredToken.isValid());
        assertTrue(expiredToken.isExpired());

        // Test used token
        VerificationToken usedToken = VerificationToken.builder()
                .token("used-token")
                .user(testUser)
                .tokenType(VerificationToken.TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(true)
                .build();

        assertFalse(usedToken.isValid());
        assertFalse(usedToken.isExpired());
        assertTrue(usedToken.getUsed());
    }

    @Test
    void testPasswordMatchingValidation() {
        // Given
        UserRegistrationRequestDto matchingPasswords = UserRegistrationRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePass123!")
                .confirmPassword("SecurePass123!")
                .build();

        UserRegistrationRequestDto nonMatchingPasswords = UserRegistrationRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePass123!")
                .confirmPassword("DifferentPass123!")
                .build();

        // When & Then
        assertTrue(matchingPasswords.isPasswordMatching());
        assertFalse(nonMatchingPasswords.isPasswordMatching());
    }

    @Test
    void testRegistrationRequestValidation() {
        // Given
        UserRegistrationRequestDto validRequest = UserRegistrationRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("SecurePass123!")
                .confirmPassword("SecurePass123!")
                .acceptTerms(true)
                .acceptPrivacyPolicy(true)
                .build();

        // When & Then
        assertTrue(validRequest.isPasswordMatching());
        assertTrue(validRequest.getAcceptTerms());
        assertTrue(validRequest.getAcceptPrivacyPolicy());
        assertTrue(validRequest.getSendWelcomeEmail());
        assertTrue(validRequest.getRequireEmailVerification());
    }
}
