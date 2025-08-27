package com.user.service.entity;

import java.util.Arrays;

/**
 * User roles for ecommerce platform
 * CUSTOMER - Regular buyers/users
 * MERCHANT - Sellers/vendors who can manage products
 * ADMIN - System administrators with full access
 * MODERATOR - Content moderators with limited admin privileges
 */
public enum Role {
    CUSTOMER("CUSTOMER"),
    MERCHANT("MERCHANT"), 
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    
    // Keep USER for backward compatibility
    @Deprecated
    USER("CUSTOMER");

    private final String name;

    Role(String role) {
        this.name = role;
    }

    public String getName() {
        return name;
    }

    public static String[] getRoles() {
        return Arrays.stream(Role.values())
                .filter(role -> role != USER) // Exclude deprecated USER role
                .map(Role::getName)
                .toArray(String[]::new);
    }

    /**
     * Check if role has admin privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if role has moderator privileges
     */
    public boolean isModerator() {
        return this == ADMIN || this == MODERATOR;
    }

    /**
     * Check if role can sell products
     */
    public boolean canSell() {
        return this == MERCHANT || this == ADMIN;
    }
}
