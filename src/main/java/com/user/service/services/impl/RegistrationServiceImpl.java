package com.user.service.services.impl;

import com.user.service.dao.UserDao;
import com.user.service.dto.request.UserRegistrationRequestDto;
import com.user.service.dto.response.UserRegistrationResponseDto;
import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.entity.VerificationToken;
import com.user.service.entity.VerificationToken.TokenType;
import com.user.service.error.UserAlreadyExistsException;
import com.user.service.repository.VerificationTokenRepository;
import com.user.service.services.EmailService;
import com.user.service.services.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of RegistrationService for user registration and account activation
 * Handles the complete user registration workflow
 */
@Service
@Slf4j
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.email.verification.expiry-hours:24}")
    private int verificationExpiryHours;

    @Override
    public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto requestDto) {
        log.info("Processing user registration for email: {}", requestDto.getEmail());

        try {
            // Check if user already exists
            if (isEmailAlreadyRegistered(requestDto.getEmail())) {
                log.warn("Registration failed: Email already registered: {}", requestDto.getEmail());
                return UserRegistrationResponseDto.userAlreadyExists(requestDto.getEmail());
            }

            if (isUsernameAlreadyTaken(requestDto.getUsername())) {
                log.warn("Registration failed: Username already taken: {}", requestDto.getUsername());
                return UserRegistrationResponseDto.validationError("Username is already taken");
            }

            // Create new user
            User user = createUserFromRequest(requestDto);
            userDao.save(user);
            log.info("User created successfully with ID: {}", user.getId());

            // Generate verification token
            String verificationToken = generateVerificationToken(user);
            log.info("Verification token generated for user: {}", user.getId());

            // Send verification email
            if (requestDto.getRequireEmailVerification()) {
                emailService.sendEmailVerification(user, verificationToken);
                log.info("Verification email sent to: {}", user.getEmail());
            }

            // Send welcome email if requested
            if (requestDto.getSendWelcomeEmail()) {
                emailService.sendWelcomeEmail(user);
                log.info("Welcome email sent to: {}", user.getEmail());
            }

            return UserRegistrationResponseDto.success(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
            );

        } catch (Exception e) {
            log.error("Error during user registration for email: {}", requestDto.getEmail(), e);
            return UserRegistrationResponseDto.systemError("Registration failed due to system error");
        }
    }

    @Override
    public boolean verifyEmail(String token) {
        log.info("Processing email verification for token: {}", token);

        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                    .orElse(null);

            if (verificationToken == null) {
                log.warn("Email verification failed: Invalid token: {}", token);
                return false;
            }

            if (verificationToken.isExpired()) {
                log.warn("Email verification failed: Token expired for user: {}", verificationToken.getUser().getId());
                return false;
            }

            if (verificationToken.getUsed()) {
                log.warn("Email verification failed: Token already used for user: {}", verificationToken.getUser().getId());
                return false;
            }

            // Mark token as used
            verificationToken.markAsUsed();
            verificationTokenRepository.save(verificationToken);

            // Update user email verification status
            User user = verificationToken.getUser();
            user.setEmailVerified(true);
            userDao.save(user);

            log.info("Email verified successfully for user: {}", user.getId());
            return true;

        } catch (Exception e) {
            log.error("Error during email verification for token: {}", token, e);
            return false;
        }
    }

    @Override
    public boolean activateAccount(String token) {
        log.info("Processing account activation for token: {}", token);

        try {
            VerificationToken activationToken = verificationTokenRepository.findByToken(token)
                    .orElse(null);

            if (activationToken == null) {
                log.warn("Account activation failed: Invalid token: {}", token);
                return false;
            }

            if (activationToken.isExpired()) {
                log.warn("Account activation failed: Token expired for user: {}", activationToken.getUser().getId());
                return false;
            }

            if (activationToken.getUsed()) {
                log.warn("Account activation failed: Token already used for user: {}", activationToken.getUser().getId());
                return false;
            }

            // Mark token as used
            activationToken.markAsUsed();
            verificationTokenRepository.save(activationToken);

            // Update user account status
            User user = activationToken.getUser();
            user.setAccountLocked(false);
            user.setEmailVerified(true);
            userDao.save(user);

            log.info("Account activated successfully for user: {}", user.getId());
            return true;

        } catch (Exception e) {
            log.error("Error during account activation for token: {}", token, e);
            return false;
        }
    }

    @Override
    public boolean resendVerificationEmail(String email) {
        log.info("Processing resend verification email for: {}", email);

        try {
            User user = userDao.findByEmail(email).orElse(null);
            if (user == null) {
                log.warn("Resend verification failed: User not found: {}", email);
                return false;
            }

            if (user.getEmailVerified()) {
                log.warn("Resend verification failed: Email already verified: {}", email);
                return false;
            }

            // Generate new verification token
            String newToken = generateVerificationToken(user);
            emailService.sendEmailVerification(user, newToken);

            log.info("Verification email resent successfully to: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error during resend verification email for: {}", email, e);
            return false;
        }
    }

    @Override
    public boolean isEmailAlreadyRegistered(String email) {
        return userDao.findByEmail(email).isPresent();
    }

    @Override
    public boolean isUsernameAlreadyTaken(String username) {
        return userDao.findByUsername(username).isPresent();
    }

    @Override
    public boolean generateNewVerificationToken(String email) {
        log.info("Generating new verification token for: {}", email);

        try {
            User user = userDao.findByEmail(email).orElse(null);
            if (user == null) {
                log.warn("Generate new token failed: User not found: {}", email);
                return false;
            }

            // Invalidate existing tokens
            invalidateExistingTokens(user.getId());

            // Generate new token
            String newToken = generateVerificationToken(user);
            emailService.sendEmailVerification(user, newToken);

            log.info("New verification token generated successfully for: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error generating new verification token for: {}", email, e);
            return false;
        }
    }

    // Private helper methods

    private User createUserFromRequest(UserRegistrationRequestDto requestDto) {
        return User.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phone(requestDto.getPhone())
                .role(requestDto.getRole())
                .emailVerified(false)
                .accountLocked(true) // Account locked until email verification
                .failedLoginAttempts(0)
                .build();
    }

    private String generateVerificationToken(User user) {
        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Create verification token entity
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiryDate(LocalDateTime.now().plusHours(verificationExpiryHours))
                .used(false)
                .build();

        // Save to database
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    private void invalidateExistingTokens(Long userId) {
        List<VerificationToken> existingTokens = verificationTokenRepository.findByUserId(userId);
        for (VerificationToken token : existingTokens) {
            token.markAsUsed();
            verificationTokenRepository.save(token);
        }
    }
}
