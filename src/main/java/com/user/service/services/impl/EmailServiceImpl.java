package com.user.service.services.impl;

import com.user.service.entity.User;
import com.user.service.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of EmailService for sending emails
 * Currently logs emails (can be extended with actual email service integration)
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.email.verification.expiry-hours:24}")
    private int verificationExpiryHours;

    @Override
    public void sendEmailVerification(User user, String verificationToken) {
        String subject = "Verify Your Email Address";
        String verificationUrl = baseUrl + "/auth/verify?token=" + verificationToken;
        
        String content = buildEmailVerificationTemplate(user, verificationUrl);
        
        log.info("Sending email verification to: {} with token: {}", user.getEmail(), verificationToken);
        log.info("Verification URL: {}", verificationUrl);
        
        // TODO: Integrate with actual email service (SendGrid, AWS SES, etc.)
        // For now, just log the email content
        logEmailContent(user.getEmail(), subject, content);
    }

    @Override
    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to Our Ecommerce Platform!";
        String content = buildWelcomeEmailTemplate(user);
        
        log.info("Sending welcome email to: {}", user.getEmail());
        
        // TODO: Integrate with actual email service
        logEmailContent(user.getEmail(), subject, content);
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        String subject = "Reset Your Password";
        String resetUrl = baseUrl + "/auth/reset-password?token=" + resetToken;
        
        String content = buildPasswordResetTemplate(user, resetUrl);
        
        log.info("Sending password reset email to: {} with token: {}", user.getEmail(), resetToken);
        log.info("Reset URL: {}", resetUrl);
        
        // TODO: Integrate with actual email service
        logEmailContent(user.getEmail(), subject, content);
    }

    @Override
    public void sendAccountActivationEmail(User user, String activationToken) {
        String subject = "Activate Your Account";
        String activationUrl = baseUrl + "/auth/activate?token=" + activationToken;
        
        String content = buildAccountActivationTemplate(user, activationUrl);
        
        log.info("Sending account activation email to: {} with token: {}", user.getEmail(), activationToken);
        log.info("Activation URL: {}", activationUrl);
        
        // TODO: Integrate with actual email service
        logEmailContent(user.getEmail(), subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        log.info("Sending generic email to: {} with subject: {}", to, subject);
        
        // TODO: Integrate with actual email service
        logEmailContent(to, subject, content);
    }

    @Override
    public boolean isEmailServiceAvailable() {
        // TODO: Check actual email service health
        return true;
    }

    // Private helper methods for building email templates

    private String buildEmailVerificationTemplate(User user, String verificationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Verify Your Email</title>
            </head>
            <body>
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                    <h2 style="color: #333;">Hello %s!</h2>
                    <p>Thank you for registering with our ecommerce platform. To complete your registration, please verify your email address by clicking the button below:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Verify Email Address
                        </a>
                    </div>
                    
                    <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #666;">%s</p>
                    
                    <p>This verification link will expire in %d hours.</p>
                    
                    <p>If you didn't create an account, you can safely ignore this email.</p>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="color: #666; font-size: 12px;">
                        This is an automated email. Please do not reply to this message.
                    </p>
                </div>
            </body>
            </html>
            """, 
            user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
            verificationUrl,
            verificationUrl,
            verificationExpiryHours
        );
    }

    private String buildWelcomeEmailTemplate(User user) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome!</title>
            </head>
            <body>
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                    <h2 style="color: #333;">Welcome to Our Ecommerce Platform!</h2>
                    <p>Hello %s,</p>
                    <p>Thank you for joining our community! Your account has been successfully created and verified.</p>
                    
                    <h3 style="color: #555;">What's Next?</h3>
                    <ul>
                        <li>Complete your profile</li>
                        <li>Add your shipping addresses</li>
                        <li>Browse our products</li>
                        <li>Start shopping!</li>
                    </ul>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Start Shopping
                        </a>
                    </div>
                    
                    <p>If you have any questions, feel free to contact our support team.</p>
                    
                    <p>Happy shopping!</p>
                    <p>The Team</p>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="color: #666; font-size: 12px;">
                        This is an automated email. Please do not reply to this message.
                    </p>
                </div>
            </body>
            </html>
            """,
            user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
            baseUrl
        );
    }

    private String buildPasswordResetTemplate(User user, String resetUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Your Password</title>
            </head>
            <body>
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                    <h2 style="color: #333;">Reset Your Password</h2>
                    <p>Hello %s,</p>
                    <p>We received a request to reset your password. Click the button below to create a new password:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #dc3545; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Reset Password
                        </a>
                    </div>
                    
                    <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #666;">%s</p>
                    
                    <p>This link will expire in 1 hour for security reasons.</p>
                    
                    <p>If you didn't request a password reset, you can safely ignore this email.</p>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="color: #666; font-size: 12px;">
                        This is an automated email. Please do not reply to this message.
                    </p>
                </div>
            </body>
            </html>
            """,
            user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
            resetUrl,
            resetUrl
        );
    }

    private String buildAccountActivationTemplate(User user, String activationUrl) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Activate Your Account</title>
            </head>
            <body>
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                    <h2 style="color: #333;">Activate Your Account</h2>
                    <p>Hello %s,</p>
                    <p>Your account has been created and is ready for activation. Click the button below to activate your account:</p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #17a2b8; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Activate Account
                        </a>
                    </div>
                    
                    <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #666;">%s</p>
                    
                    <p>This activation link will expire in %d hours.</p>
                    
                    <p>If you didn't create an account, you can safely ignore this email.</p>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="color: #666; font-size: 12px;">
                        This is an automated email. Please do not reply to this message.
                    </p>
                </div>
            </body>
            </html>
            """,
            user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
            activationUrl,
            activationUrl,
            verificationExpiryHours
        );
    }

    private void logEmailContent(String to, String subject, String content) {
        log.info("=== EMAIL SENT ===");
        log.info("To: {}", to);
        log.info("Subject: {}", subject);
        log.info("Content Length: {} characters", content.length());
        log.info("==================");
    }
}
