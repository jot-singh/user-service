package com.user.service.services;

import com.user.service.dto.request.UserRegistrationRequestDto;
import com.user.service.dto.response.UserRegistrationResponseDto;

/**
 * Service interface for user registration and account activation
 * Handles the complete user registration workflow
 */
public interface RegistrationService {

    /**
     * Register a new user
     * @param requestDto User registration request data
     * @return Registration response with user details and verification status
     */
    UserRegistrationResponseDto registerUser(UserRegistrationRequestDto requestDto);

    /**
     * Verify user email with verification token
     * @param token Verification token from email
     * @return true if verification successful, false otherwise
     */
    boolean verifyEmail(String token);

    /**
     * Activate user account with activation token
     * @param token Activation token from email
     * @return true if activation successful, false otherwise
     */
    boolean activateAccount(String token);

    /**
     * Resend verification email
     * @param email User's email address
     * @return true if email sent successfully, false otherwise
     */
    boolean resendVerificationEmail(String email);

    /**
     * Check if email is already registered
     * @param email Email address to check
     * @return true if email is already registered, false otherwise
     */
    boolean isEmailAlreadyRegistered(String email);

    /**
     * Check if username is already taken
     * @param username Username to check
     * @return true if username is already taken, false otherwise
     */
    boolean isUsernameAlreadyTaken(String username);

    /**
     * Generate new verification token for user
     * @param email User's email address
     * @return true if new token generated and email sent, false otherwise
     */
    boolean generateNewVerificationToken(String email);
}
