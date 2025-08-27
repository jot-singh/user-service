package com.user.service.security;

import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Custom permission evaluator for fine-grained access control
 * Handles user-specific permissions and role-based access
 */
@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserService userService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String requestedPermission = permission.toString();
        log.debug("Checking permission: {} for target: {}", requestedPermission, targetDomainObject);

        // Check if user has ADMIN role (full access)
        if (hasRole(authentication, Role.ADMIN)) {
            return true;
        }

        // Handle different permission types
        switch (requestedPermission) {
            case "READ":
                return hasReadPermission(authentication, targetDomainObject);
            case "WRITE":
                return hasWritePermission(authentication, targetDomainObject);
            case "DELETE":
                return hasDeletePermission(authentication, targetDomainObject);
            case "MANAGE":
                return hasManagePermission(authentication, targetDomainObject);
            default:
                log.warn("Unknown permission requested: {}", requestedPermission);
                return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String requestedPermission = permission.toString();
        log.debug("Checking permission: {} for target type: {} with ID: {}", requestedPermission, targetType, targetId);

        // Check if user has ADMIN role (full access)
        if (hasRole(authentication, Role.ADMIN)) {
            return true;
        }

        // Handle different target types
        switch (targetType.toLowerCase()) {
            case "user":
                return hasUserPermission(authentication, targetId, requestedPermission);
            case "address":
                return hasAddressPermission(authentication, targetId, requestedPermission);
            default:
                log.warn("Unknown target type: {}", targetType);
                return false;
        }
    }

    private boolean hasReadPermission(Authentication authentication, Object target) {
        // Only authenticated users can read information
        return authentication != null && authentication.isAuthenticated();
    }

    private boolean hasWritePermission(Authentication authentication, Object target) {
        // Users can only write to their own data
        if (target instanceof User) {
            return isOwner(authentication, (User) target);
        }
        return false;
    }

    private boolean hasDeletePermission(Authentication authentication, Object target) {
        // Users can only delete their own data
        if (target instanceof User) {
            return isOwner(authentication, (User) target);
        }
        return false;
    }

    private boolean hasManagePermission(Authentication authentication, Object target) {
        // Only ADMIN and MODERATOR can manage
        return hasRole(authentication, Role.ADMIN) || hasRole(authentication, Role.MODERATOR);
    }

    private boolean hasUserPermission(Authentication authentication, Serializable targetId, String permission) {
        try {
            Long userId = Long.valueOf(targetId.toString());
            String currentUsername = authentication.getName();
            
            // Get current user from database
            User currentUser = userService.getUserById(userId);
            if (currentUser == null) {
                return false;
            }

            switch (permission) {
                case "READ":
                    // Users can read their own profile, admins and moderators can read all
                    return currentUser.getEmail().equals(currentUsername) || 
                           hasRole(authentication, Role.ADMIN) || 
                           hasRole(authentication, Role.MODERATOR);
                case "WRITE":
                    // Users can only modify their own profile
                    return currentUser.getEmail().equals(currentUsername);
                case "DELETE":
                    // Users can only delete their own account
                    return currentUser.getEmail().equals(currentUsername);
                case "MANAGE":
                    // Only admins can manage users
                    return hasRole(authentication, Role.ADMIN);
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Error checking user permission", e);
            return false;
        }
    }

    private boolean hasAddressPermission(Authentication authentication, Serializable targetId, String permission) {
        try {
            Long addressId = Long.valueOf(targetId.toString());
            String currentUsername = authentication.getName();
            
            // For address permissions, we need to check if the address belongs to the current user
            // This is a simplified check - in a real implementation, you'd query the address ownership
            switch (permission) {
                case "READ":
                    // Users can read their own addresses, admins can read all
                    return hasRole(authentication, Role.ADMIN) || 
                           hasRole(authentication, Role.MODERATOR);
                case "WRITE":
                case "DELETE":
                    // Users can only modify their own addresses
                    // This would require additional logic to check address ownership
                    return hasRole(authentication, Role.ADMIN) || 
                           hasRole(authentication, Role.MODERATOR);
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Error checking address permission", e);
            return false;
        }
    }

    private boolean isOwner(Authentication authentication, User targetUser) {
        return targetUser.getEmail().equals(authentication.getName());
    }

    private boolean hasRole(Authentication authentication, Role role) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.getName()));
    }
}
