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
     * Get user profile by user ID
     * GET /users/{userId}/profile
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        log.info("Getting user profile for user ID: {}", userId);
        
        try {
            UserResponseDto userProfile = userService.getUserProfile(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            log.error("Error getting user profile for user ID: {}", userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Update user profile information
     * PUT /users/{userId}/profile
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequestDto requestDto) {
        log.info("Updating user profile for user ID: {}", userId);
        
        try {
            UserResponseDto updatedProfile = userService.updateUserProfile(userId, requestDto);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Error updating user profile for user ID: {}", userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Get all addresses for a user
     * GET /users/{userId}/addresses
     */
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(@PathVariable Long userId) {
        log.info("Getting addresses for user ID: {}", userId);
        
        try {
            List<AddressResponseDto> addresses = userService.getUserAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            log.error("Error getting addresses for user ID: {}", userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Get specific address by ID
     * GET /users/{userId}/addresses/{addressId}
     */
    @GetMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Getting address ID: {} for user ID: {}", addressId, userId);
        
        try {
            AddressResponseDto address = userService.getAddress(userId, addressId);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            log.error("Error getting address ID: {} for user ID: {}", addressId, userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Create new address for user
     * POST /users/{userId}/addresses
     */
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressResponseDto> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        log.info("Creating new address for user ID: {}", userId);
        
        try {
            AddressResponseDto createdAddress = userService.createAddress(userId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
        } catch (Exception e) {
            log.error("Error creating address for user ID: {}", userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Update existing address
     * PUT /users/{userId}/addresses/{addressId}
     */
    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto requestDto) {
        log.info("Updating address ID: {} for user ID: {}", addressId, userId);
        
        try {
            AddressResponseDto updatedAddress = userService.updateAddress(userId, addressId, requestDto);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            log.error("Error updating address ID: {} for user ID: {}", addressId, userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Delete address
     * DELETE /users/{userId}/addresses/{addressId}
     */
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Deleting address ID: {} for user ID: {}", addressId, userId);
        
        try {
            userService.deleteAddress(userId, addressId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting address ID: {} for user ID: {}", addressId, userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Set default address for user
     * PUT /users/{userId}/addresses/{addressId}/default
     */
    @PutMapping("/{userId}/addresses/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        log.info("Setting default address ID: {} for user ID: {}", addressId, userId);
        
        try {
            AddressResponseDto defaultAddress = userService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error setting default address ID: {} for user ID: {}", addressId, userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Get default address for user
     * GET /users/{userId}/addresses/default
     */
    @GetMapping("/{userId}/addresses/default")
    public ResponseEntity<AddressResponseDto> getDefaultAddress(@PathVariable Long userId) {
        log.info("Getting default address for user ID: {}", userId);
        
        try {
            AddressResponseDto defaultAddress = userService.getDefaultAddress(userId);
            return ResponseEntity.ok(defaultAddress);
        } catch (Exception e) {
            log.error("Error getting default address for user ID: {}", userId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
}
