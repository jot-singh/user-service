package com.user.service.repository;

import com.user.service.entity.VerificationToken;
import com.user.service.entity.VerificationToken.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for verification tokens
 * Handles database operations for email verification and account activation
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Find verification token by token value
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Find verification token by user ID and token type
     */
    Optional<VerificationToken> findByUserIdAndTokenType(Long userId, TokenType tokenType);

    /**
     * Find all verification tokens for a user
     */
    List<VerificationToken> findByUserId(Long userId);

    /**
     * Find all expired tokens
     */
    List<VerificationToken> findByExpiryDateBefore(LocalDateTime dateTime);

    /**
     * Find all unused tokens
     */
    List<VerificationToken> findByUsedFalse();

    /**
     * Find all unused and expired tokens
     */
    List<VerificationToken> findByUsedFalseAndExpiryDateBefore(LocalDateTime dateTime);

    /**
     * Delete expired tokens
     */
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiryDate < :dateTime")
    void deleteExpiredTokens(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Mark token as used
     */
    @Modifying
    @Query("UPDATE VerificationToken vt SET vt.used = true WHERE vt.id = :tokenId")
    void markTokenAsUsed(@Param("tokenId") Long tokenId);

    /**
     * Count active tokens for a user
     */
    long countByUserIdAndUsedFalseAndExpiryDateAfter(Long userId, LocalDateTime dateTime);

    /**
     * Check if user has active verification token
     */
    boolean existsByUserIdAndUsedFalseAndExpiryDateAfter(Long userId, LocalDateTime dateTime);
}
