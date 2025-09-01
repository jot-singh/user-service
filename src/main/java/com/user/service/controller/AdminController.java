package com.user.service.controller;

import com.user.service.dto.response.UserResponseDto;
import com.user.service.entity.Role;
import com.user.service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for administrative operations
 * Provides endpoints for user management with role-based access control
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * Get all users with pagination
     * GET /admin/users
     * Requires ADMIN role
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Admin getting all users - page: {}, size: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserResponseDto> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error getting all users", e);
            throw e;
        }
    }

    /**
     * Get users by role
     * GET /admin/users/role/{role}
     * Requires ADMIN role
     */
    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable String role) {
        log.info("Admin getting users by role: {}", role);
        
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<UserResponseDto> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role: {}", role);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error getting users by role: {}", role, e);
            throw e;
        }
    }

    /**
     * Get user by ID (admin access)
     * GET /admin/users/{userId}
     * Requires ADMIN role
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        log.info("Admin getting user by ID: {}", userId);
        
        try {
            UserResponseDto user = userService.getUserProfile(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", userId, e);
            throw e;
        }
    }

    /**
     * Lock user account
     * POST /admin/users/{userId}/lock
     * Requires ADMIN role
     */
    @PostMapping("/users/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> lockUserAccount(@PathVariable Long userId) {
        log.info("Admin locking user account: {}", userId);
        
        try {
            UserResponseDto lockedUser = userService.lockUserAccount(userId);
            return ResponseEntity.ok(lockedUser);
        } catch (Exception e) {
            log.error("Error locking user account: {}", userId, e);
            throw e;
        }
    }

    /**
     * Unlock user account
     * POST /admin/users/{userId}/unlock
     * Requires ADMIN role
     */
    @PostMapping("/users/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> unlockUserAccount(@PathVariable Long userId) {
        log.info("Admin unlocking user account: {}", userId);
        
        try {
            UserResponseDto unlockedUser = userService.unlockUserAccount(userId);
            return ResponseEntity.ok(unlockedUser);
        } catch (Exception e) {
            log.error("Error unlocking user account: {}", userId, e);
            throw e;
        }
    }

    /**
     * Change user role
     * PUT /admin/users/{userId}/role
     * Requires ADMIN role
     */
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> changeUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole) {
        log.info("Admin changing user role: {} to {}", userId, newRole);
        
        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            UserResponseDto updatedUser = userService.changeUserRole(userId, role);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role: {}", newRole);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error changing user role: {} to {}", userId, newRole, e);
            throw e;
        }
    }

    /**
     * Delete user account
     * DELETE /admin/users/{userId}
     * Requires ADMIN role
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable Long userId) {
        log.info("Admin deleting user account: {}", userId);
        
        try {
            userService.deleteUserAccount(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting user account: {}", userId, e);
            throw e;
        }
    }

    /**
     * Get system statistics
     * GET /admin/stats
     * Requires ADMIN role
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatsResponse> getSystemStats() {
        log.info("Admin getting system statistics");
        
        try {
            AdminStatsResponse stats = userService.getSystemStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting system statistics", e);
            throw e;
        }
    }

    /**
     * Response DTO for system statistics
     */
    public static class AdminStatsResponse {
        private long totalUsers;
        private long activeUsers;
        private long lockedUsers;
        private long customers;
        private long merchants;
        private long admins;
        private long moderators;

        // Constructors
        public AdminStatsResponse() {}

        public AdminStatsResponse(long totalUsers, long activeUsers, long lockedUsers, 
                                long customers, long merchants, long admins, long moderators) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.lockedUsers = lockedUsers;
            this.customers = customers;
            this.merchants = merchants;
            this.admins = admins;
            this.moderators = moderators;
        }

        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

        public long getLockedUsers() { return lockedUsers; }
        public void setLockedUsers(long lockedUsers) { this.lockedUsers = lockedUsers; }

        public long getCustomers() { return customers; }
        public void setCustomers(long customers) { this.customers = customers; }

        public long getMerchants() { return merchants; }
        public void setMerchants(long merchants) { this.merchants = merchants; }

        public long getAdmins() { return admins; }
        public void setAdmins(long admins) { this.admins = admins; }

        public long getModerators() { return moderators; }
        public void setModerators(long moderators) { this.moderators = moderators; }
    }
}
