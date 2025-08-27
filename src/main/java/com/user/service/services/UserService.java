package com.user.service.services;

import com.user.service.dto.request.AddressRequestDto;
import com.user.service.dto.request.UserProfileRequestDto;
import com.user.service.dto.response.AddressResponseDto;
import com.user.service.dto.response.UserResponseDto;
import com.user.service.entity.User;
import com.user.service.entity.Role;
import com.user.service.controller.AdminController.AdminStatsResponse;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user profile management
 */
public interface UserService {
    
    /**
     * Get user profile by user ID
     */
    UserResponseDto getUserProfile(Long userId);
    
    /**
     * Update user profile information
     */
    UserResponseDto updateUserProfile(Long userId, UserProfileRequestDto requestDto);
    
    /**
     * Get all addresses for a user
     */
    List<AddressResponseDto> getUserAddresses(Long userId);
    
    /**
     * Get address by ID (validates user ownership)
     */
    AddressResponseDto getAddress(Long userId, Long addressId);
    
    /**
     * Create new address for user
     */
    AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto);
    
    /**
     * Update existing address
     */
    AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto requestDto);
    
    /**
     * Delete address
     */
    void deleteAddress(Long userId, Long addressId);
    
    /**
     * Set default address for user
     */
    AddressResponseDto setDefaultAddress(Long userId, Long addressId);
    
    /**
     * Get default address for user
     */
    AddressResponseDto getDefaultAddress(Long userId);
    
    /**
     * Get user by ID (internal use)
     */
    User getUserById(Long userId);
    
    /**
     * Check if user exists and is active
     */
    boolean isUserActive(Long userId);
    
    // Admin methods for role-based access control
    
    /**
     * Get all users with pagination (admin only)
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    
    /**
     * Get users by role (admin only)
     */
    List<UserResponseDto> getUsersByRole(Role role);
    
    /**
     * Lock user account (admin only)
     */
    UserResponseDto lockUserAccount(Long userId);
    
    /**
     * Unlock user account (admin only)
     */
    UserResponseDto unlockUserAccount(Long userId);
    
    /**
     * Change user role (admin only)
     */
    UserResponseDto changeUserRole(Long userId, Role newRole);
    
    /**
     * Delete user account (admin only)
     */
    void deleteUserAccount(Long userId);
    
    /**
     * Get system statistics (admin only)
     */
    AdminStatsResponse getSystemStatistics();
}
