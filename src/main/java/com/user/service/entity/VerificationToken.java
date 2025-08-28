package com.user.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Verification token entity for email verification and account activation
 * Used during user registration process
 */
@Entity
@Getter
@Setter
@Table(name = "verification_tokens", indexes = {
    @Index(name = "idx_verification_token", columnList = "token"),
    @Index(name = "idx_verification_user_id", columnList = "user_id"),
    @Index(name = "idx_verification_expiry", columnList = "expiry_date")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken extends BaseVO {

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    @Builder.Default
    private TokenType tokenType = TokenType.EMAIL_VERIFICATION;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Check if token is expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Check if token is valid (not expired and not used)
     */
    public boolean isValid() {
        return !isExpired() && !used;
    }

    /**
     * Mark token as used
     */
    public void markAsUsed() {
        this.used = true;
    }

    /**
     * Token types for different verification purposes
     */
    public enum TokenType {
        EMAIL_VERIFICATION("Email Verification"),
        PASSWORD_RESET("Password Reset"),
        ACCOUNT_ACTIVATION("Account Activation");

        private final String displayName;

        TokenType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
