package com.user.service.controller;

import com.user.service.dto.request.AddressRequestDto;
import com.user.service.dto.request.UserProfileRequestDto;
import com.user.service.dto.response.AddressResponseDto;
import com.user.service.dto.response.UserResponseDto;
import com.user.service.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for user profile management operations
 * Provides CRUD operations for user profiles and addresses
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user's profile
     * GET /users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile() {
        Long currentUserId = getCurrentUserId();
        log.info("Getting current user profile for user ID: {}", currentUserId);

        try {
            UserResponseDto userProfile = userService.getUserProfile(currentUserId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error getting user profile for user ID: {}", currentUserId, e);
            throw e;
        }
    }

    /**
     * Get user profile by user ID (Admin only)
     * GET /users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        log.info("Admin getting user profile for user ID: {}", userId);

        try {
            UserResponseDto userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error getting user profile for user ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Update current user's profile
     * PUT /users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateCurrentUserProfile(
            @Valid @RequestBody UserProfileRequestDto requestDto) {
        Long currentUserId = getCurrentUserId();
        log.info("Updating current user profile for user ID: {}", currentUserId);

        try {
            UserResponseDto updatedProfile = userService.updateUserProfile(currentUserId, requestDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Error updating user profile for user ID: {}", currentUserId, e);
            throw e;
        }
    }

    /**
     * Update user profile by ID (Admin only)
     * PUT /users/{userId}/profile
     */
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequestDto requestDto) {
        log.info("Admin updating user profile for user ID: {}", userId);

        try {
            UserResponseDto updatedProfile = userService.updateUserProfile(userId, requestDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Error updating user profile for user ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Get current user's addresses
     * GET /users/addresses
     */
    @GetMapping("/addresses")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<AddressResponseDto>> getCurrentUserAddresses() {
        Long currentUserId = getCurrentUserId();
        log.info("Getting addresses for current user ID: {}", currentUserId);

        try {
            List<AddressResponseDto> addresses = userService.getUserAddresses(currentUserId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            log.error("Error getting addresses for user ID: {}", currentUserId, e);
            throw e;
        }
    }

    /**
     * Get user addresses by user ID (Admin only)
     * GET /users/{userId}/addresses
     */
    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(@PathVariable Long userId) {
        log.info("Admin getting addresses for user ID: {}", userId);

        try {
            List<AddressResponseDto> addresses = userService.getUserAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            log.error("Error getting addresses for user ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Get current user's specific address
     * GET /users/addresses/{addressId}
     */
    @GetMapping("/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> getCurrentUserAddress(@PathVariable Long addressId) {
        Long currentUserId = getCurrentUserId();
        log.info("Getting address ID: {} for current user ID: {}", addressId, currentUserId);

        try {
            AddressResponseDto address = userService.getAddress(currentUserId, addressId);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            log.error("Error getting address ID: {} for user ID: {}", addressId, currentUserId, e);
            throw e;
        }
    }

    /**
     * Get user address by user ID and address ID (Admin only)
     * GET /users/{userId}/addresses/{addressId}
     */
    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> getUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Admin getting address ID: {} for user ID: {}", addressId, userId);

        try {
            AddressResponseDto address = userService.getAddress(userId, addressId);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            log.error("Error getting address ID: {} for user ID: {}", addressId, userId, e);
            throw e;
        }
    }

    // Helper method to get current user ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // This would need to be implemented based on your JWT token structure
        // For now, returning a placeholder - you'll need to extract user ID from JWT
        return 1L; // TODO: Extract from JWT token
    }
    /**
     * Create new address for current user
     * POST /users/addresses
     */
    @PostMapping("/addresses")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> createCurrentUserAddress(
            @Valid @RequestBody AddressRequestDto requestDto) {
        Long currentUserId = getCurrentUserId();
        log.info("Creating new address for current user ID: {}", currentUserId);

        try {
            AddressResponseDto createdAddress = userService.createAddress(currentUserId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (Exception e) {
            log.error("Error creating address for user ID: {}", currentUserId, e);
            throw e;
        }
    }

    /**
     * Create new address for user (Admin only)
     * POST /users/{userId}/addresses
     */
    @PostMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> createUserAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        log.info("Admin creating new address for user ID: {}", userId);

        try {
            AddressResponseDto createdAddress = userService.createAddress(userId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (Exception e) {
            log.error("Error creating address for user ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Update current user's address
     * PUT /users/addresses/{addressId}
     */
    @PutMapping("/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> updateCurrentUserAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        Long currentUserId = getCurrentUserId();
        log.info("Updating address ID: {} for current user ID: {}", addressId, currentUserId);

        try {
            AddressResponseDto updatedAddress = userService.updateAddress(currentUserId, addressId, requestDto);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            log.error("Error updating address ID: {} for user ID: {}", addressId, currentUserId, e);
            throw e;
        }
    }

    /**
     * Update user address (Admin only)
     * PUT /users/{userId}/addresses/{addressId}
     */
    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> updateUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        log.info("Admin updating address ID: {} for user ID: {}", addressId, userId);

        try {
            AddressResponseDto updatedAddress = userService.updateAddress(userId, addressId, requestDto);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            log.error("Error updating address ID: {} for user ID: {}", addressId, userId, e);
            throw e;
        }
    }

    /**
     * Delete current user's address
     * DELETE /users/addresses/{addressId}
     */
    @DeleteMapping("/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCurrentUserAddress(@PathVariable Long addressId) {
        Long currentUserId = getCurrentUserId();
        log.info("Deleting address ID: {} for current user ID: {}", addressId, currentUserId);

        try {
            userService.deleteAddress(currentUserId, addressId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting address ID: {} for user ID: {}", addressId, currentUserId, e);
            throw e;
        }
    }

    /**
     * Delete user address (Admin only)
     * DELETE /users/{userId}/addresses/{addressId}
     */
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Admin deleting address ID: {} for user ID: {}", addressId, userId);

        try {
            userService.deleteAddress(userId, addressId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting address ID: {} for user ID: {}", addressId, userId, e);
            throw e;
        }
    }

    /**
     * Set default address for current user
     * PUT /users/addresses/{addressId}/default
     */
    @PutMapping("/addresses/{addressId}/default")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> setCurrentUserDefaultAddress(@PathVariable Long addressId) {
        Long currentUserId = getCurrentUserId();
        log.info("Setting default address ID: {} for current user ID: {}", addressId, currentUserId);

        try {
            AddressResponseDto defaultAddress = userService.setDefaultAddress(currentUserId, addressId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error setting default address ID: {} for user ID: {}", addressId, currentUserId, e);
            throw e;
        }
    }

    /**
     * Set default address for user (Admin only)
     * PUT /users/{userId}/addresses/{addressId}/default
     */
    @PutMapping("/{userId}/addresses/{addressId}/default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> setUserDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Admin setting default address ID: {} for user ID: {}", addressId, userId);

        try {
            AddressResponseDto defaultAddress = userService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error setting default address ID: {} for user ID: {}", addressId, userId, e);
            throw e;
        }
    }

    /**
     * Get default address for current user
     * GET /users/addresses/default
     */
    @GetMapping("/addresses/default")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> getCurrentUserDefaultAddress() {
        Long currentUserId = getCurrentUserId();
        log.info("Getting default address for current user ID: {}", currentUserId);

        try {
            AddressResponseDto defaultAddress = userService.getDefaultAddress(currentUserId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error getting default address for user ID: {}", currentUserId, e);
            throw e;
        }
    }

    /**
     * Get default address for user (Admin only)
     * GET /users/{userId}/addresses/default
     */
    @GetMapping("/{userId}/addresses/default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDto> getUserDefaultAddress(@PathVariable Long userId) {
        log.info("Admin getting default address for user ID: {}", userId);

        try {
            AddressResponseDto defaultAddress = userService.getDefaultAddress(userId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error getting default address for user ID: {}", userId, e);
            throw e;
        }
    }
}
