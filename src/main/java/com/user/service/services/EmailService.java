package com.user.service.services;

import com.user.service.entity.User;

/**
 * Service interface for email operations
 * Handles sending emails for user registration, verification, and notifications
 */
public interface EmailService {

    /**
     * Send email verification email to user
     * @param user The user to send verification email to
     * @param verificationToken The verification token
     */
    void sendEmailVerification(User user, String verificationToken);

    /**
     * Send welcome email to newly registered user
     * @param user The newly registered user
     */
    void sendWelcomeEmail(User user);

    /**
     * Send password reset email
     * @param user The user requesting password reset
     * @param resetToken The password reset token
     */
    void sendPasswordResetEmail(User user, String resetToken);

    /**
     * Send account activation email
     * @param user The user to activate
     * @param activationToken The activation token
     */
    void sendAccountActivationEmail(User user, String activationToken);

    /**
     * Send generic email
     * @param to Recipient email address
     * @param subject Email subject
     * @param content Email content (HTML)
     */
    void sendEmail(String to, String subject, String content);

    /**
     * Check if email service is available
     * @return true if email service is operational
     */
    boolean isEmailServiceAvailable();
}
