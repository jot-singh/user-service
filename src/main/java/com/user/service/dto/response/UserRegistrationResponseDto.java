package com.user.service.dto.response;

import com.user.service.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user registration responses
 * Provides user details and next steps for account activation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponseDto {

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private Boolean emailVerified;
    private Boolean accountActive;
    private LocalDateTime createdAt;
    private LocalDateTime emailVerificationExpiry;

    // Registration status and next steps
    private String status;
    private String message;
    private Boolean requiresEmailVerification;
    private Boolean requiresAccountActivation;
    private String nextSteps;

    // Verification information
    private Boolean verificationEmailSent;
    private String verificationEmailSentTo;
    private LocalDateTime verificationEmailSentAt;

    // Welcome information
    private Boolean welcomeEmailSent;
    private LocalDateTime welcomeEmailSentAt;

    /**
     * Create a successful registration response
     */
    public static UserRegistrationResponseDto success(String userId, String username, String email, 
                                                    String firstName, String lastName, Role role) {
        return UserRegistrationResponseDto.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .emailVerified(false)
                .accountActive(false)
                .createdAt(LocalDateTime.now())
                .status("SUCCESS")
                .message("User registered successfully. Please check your email for verification.")
                .requiresEmailVerification(true)
                .requiresAccountActivation(false)
                .nextSteps("Check your email and click the verification link to activate your account.")
                .verificationEmailSent(true)
                .verificationEmailSentTo(email)
                .verificationEmailSentAt(LocalDateTime.now())
                .welcomeEmailSent(false)
                .build();
    }

    /**
     * Create a response for existing user
     */
    public static UserRegistrationResponseDto userAlreadyExists(String email) {
        return UserRegistrationResponseDto.builder()
                .email(email)
                .status("USER_EXISTS")
                .message("A user with this email already exists.")
                .requiresEmailVerification(false)
                .requiresAccountActivation(false)
                .nextSteps("Try logging in or use the forgot password feature.")
                .verificationEmailSent(false)
                .welcomeEmailSent(false)
                .build();
    }

    /**
     * Create a response for validation errors
     */
    public static UserRegistrationResponseDto validationError(String message) {
        return UserRegistrationResponseDto.builder()
                .status("VALIDATION_ERROR")
                .message(message)
                .requiresEmailVerification(false)
                .requiresAccountActivation(false)
                .nextSteps("Please correct the errors and try again.")
                .verificationEmailSent(false)
                .welcomeEmailSent(false)
                .build();
    }

    /**
     * Create a response for system errors
     */
    public static UserRegistrationResponseDto systemError(String message) {
        return UserRegistrationResponseDto.builder()
                .status("SYSTEM_ERROR")
                .message(message)
                .requiresEmailVerification(false)
                .requiresAccountActivation(false)
                .nextSteps("Please try again later or contact support.")
                .verificationEmailSent(false)
                .welcomeEmailSent(false)
                .build();
    }
}
