package com.user.service.repository;

import com.user.service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Address entity operations
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    /**
     * Find all addresses for a specific user
     */
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);
    
    /**
     * Find default address for a user
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
    
    /**
     * Find addresses by user ID and label
     */
    List<Address> findByUserIdAndLabelIgnoreCase(Long userId, String label);
    
    /**
     * Count addresses for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Check if user has any addresses
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Find address by user ID and address ID (for security validation)
     */
    Optional<Address> findByUserIdAndId(Long userId, Long addressId);
    
    /**
     * Reset all default flags for a user (before setting a new default)
     */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultFlagsByUserId(@Param("userId") Long userId);
    
    /**
     * Find addresses by user ID with pagination
     */
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findByUserIdWithPagination(@Param("userId") Long userId);
}
