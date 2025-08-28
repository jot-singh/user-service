package com.user.service.controller;

import com.user.service.dto.request.UserRegistrationRequestDto;
import com.user.service.dto.response.UserRegistrationResponseDto;
import com.user.service.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for user registration and account activation
 * Handles the complete user registration workflow
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    /**
     * Register a new user
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDto> registerUser(
            @Valid @RequestBody UserRegistrationRequestDto requestDto) {
        log.info("Processing user registration request for email: {}", requestDto.getEmail());
        
        try {
            UserRegistrationResponseDto response = registrationService.registerUser(requestDto);
            
            if ("SUCCESS".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else if ("USER_EXISTS".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else if ("VALIDATION_ERROR".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            log.error("Error during user registration for email: {}", requestDto.getEmail(), e);
            UserRegistrationResponseDto errorResponse = UserRegistrationResponseDto.systemError(
                "Registration failed due to system error"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verify user email with verification token
     * GET /auth/verify?token={token}
     */
    @GetMapping("/verify")
    public ResponseEntity<Object> verifyEmail(@RequestParam String token) {
        log.info("Processing email verification request for token: {}", token);
        
        try {
            boolean verified = registrationService.verifyEmail(token);
            
            if (verified) {
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Email verified successfully. Your account is now active.",
                    "verified", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Email verification failed. The token may be invalid, expired, or already used.",
                    "verified", false
                ));
            }
            
        } catch (Exception e) {
            log.error("Error during email verification for token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "ERROR",
                "message", "Email verification failed due to system error.",
                "verified", false
            ));
        }
    }

    /**
     * Activate user account with activation token
     * GET /auth/activate?token={token}
     */
    @GetMapping("/activate")
    public ResponseEntity<Object> activateAccount(@RequestParam String token) {
        log.info("Processing account activation request for token: {}", token);
        
        try {
            boolean activated = registrationService.activateAccount(token);
            
            if (activated) {
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Account activated successfully. You can now log in.",
                    "activated", true
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Account activation failed. The token may be invalid, expired, or already used.",
                    "activated", false
                ));
            }
            
        } catch (Exception e) {
            log.error("Error during account activation for token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "ERROR",
                "message", "Account activation failed due to system error.",
                "activated", false
            ));
        }
    }

    /**
     * Resend verification email
     * POST /auth/resend-verification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<Object> resendVerificationEmail(@RequestParam String email) {
        log.info("Processing resend verification email request for: {}", email);
        
        try {
            boolean sent = registrationService.resendVerificationEmail(email);
            
            if (sent) {
                return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Verification email sent successfully. Please check your inbox.",
                    "email", email
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Failed to send verification email. The email may not be registered or already verified.",
                    "email", email
                ));
            }
            
        } catch (Exception e) {
            log.error("Error during resend verification email for: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "ERROR",
                "message", "Failed to send verification email due to system error.",
                "email", email
            ));
        }
    }

    /**
     * Check if email is available for registration
     * GET /auth/check-email?email={email}
     */
    @GetMapping("/check-email")
    public ResponseEntity<Object> checkEmailAvailability(@RequestParam String email) {
        log.info("Checking email availability for: {}", email);
        
        try {
            boolean available = !registrationService.isEmailAlreadyRegistered(email);
            
            return ResponseEntity.ok(Map.of(
                "email", email,
                "available", available,
                "message", available ? "Email is available for registration" : "Email is already registered"
            ));
            
        } catch (Exception e) {
            log.error("Error checking email availability for: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "email", email,
                "available", false,
                "message", "Error checking email availability"
            ));
        }
    }

    /**
     * Check if username is available for registration
     * GET /auth/check-username?username={username}
     */
    @GetMapping("/check-username")
    public ResponseEntity<Object> checkUsernameAvailability(@RequestParam String username) {
        log.info("Checking username availability for: {}", username);
        
        try {
            boolean available = !registrationService.isUsernameAlreadyTaken(username);
            
            return ResponseEntity.ok(Map.of(
                "username", username,
                "available", available,
                "message", available ? "Username is available for registration" : "Username is already taken"
            ));
            
        } catch (Exception e) {
            log.error("Error checking username availability for: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "username", username,
                "available", false,
                "message", "Error checking username availability"
            ));
        }
    }
}
