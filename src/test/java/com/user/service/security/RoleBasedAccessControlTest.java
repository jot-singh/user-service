package com.user.service.security;

import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.entity.BaseVO;
import com.user.service.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for Role-Based Access Control (RBAC) system
 * Tests custom permission evaluator and role-based access
 */
@ExtendWith(MockitoExtension.class)
class RoleBasedAccessControlTest {

    @Mock
    private UserService userService;

    private CustomPermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setUp() {
        permissionEvaluator = new CustomPermissionEvaluator();
        // Use reflection to set the mocked userService
        try {
            java.lang.reflect.Field field = CustomPermissionEvaluator.class.getDeclaredField("userService");
            field.setAccessible(true);
            field.set(permissionEvaluator, userService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set userService in permission evaluator", e);
        }
    }

    @Test
    void testAdminRoleHasFullAccess() {
        // Given
        Authentication adminAuth = new TestingAuthenticationToken(
            "admin@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        User targetUser = createTestUser(1L, "user@test.com", Role.CUSTOMER);

        // When & Then
        assertTrue(permissionEvaluator.hasPermission(adminAuth, targetUser, "READ"));
        assertTrue(permissionEvaluator.hasPermission(adminAuth, targetUser, "WRITE"));
        assertTrue(permissionEvaluator.hasPermission(adminAuth, targetUser, "DELETE"));
        assertTrue(permissionEvaluator.hasPermission(adminAuth, targetUser, "MANAGE"));
    }

    @Test
    void testCustomerRoleLimitedAccess() {
        // Given
        Authentication customerAuth = new TestingAuthenticationToken(
            "customer@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        User ownUser = createTestUser(1L, "customer@test.com", Role.CUSTOMER);
        User otherUser = createTestUser(2L, "other@test.com", Role.CUSTOMER);

        // When & Then - Customer can read public info
        assertTrue(permissionEvaluator.hasPermission(customerAuth, ownUser, "READ"));
        assertTrue(permissionEvaluator.hasPermission(customerAuth, otherUser, "READ"));

        // Customer can only write to own data
        assertTrue(permissionEvaluator.hasPermission(customerAuth, ownUser, "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(customerAuth, otherUser, "WRITE"));

        // Customer can only delete own data
        assertTrue(permissionEvaluator.hasPermission(customerAuth, ownUser, "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(customerAuth, otherUser, "DELETE"));

        // Customer cannot manage
        assertFalse(permissionEvaluator.hasPermission(customerAuth, ownUser, "MANAGE"));
        assertFalse(permissionEvaluator.hasPermission(customerAuth, otherUser, "MANAGE"));
    }

    @Test
    void testModeratorRoleAccess() {
        // Given
        Authentication moderatorAuth = new TestingAuthenticationToken(
            "moderator@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_MODERATOR"))
        );
        User targetUser = createTestUser(1L, "user@test.com", Role.CUSTOMER);

        // When & Then - Moderator can read and manage
        assertTrue(permissionEvaluator.hasPermission(moderatorAuth, targetUser, "READ"));
        assertTrue(permissionEvaluator.hasPermission(moderatorAuth, targetUser, "MANAGE"));

        // But cannot write or delete other users' data
        assertFalse(permissionEvaluator.hasPermission(moderatorAuth, targetUser, "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(moderatorAuth, targetUser, "DELETE"));
    }

    @Test
    void testMerchantRoleAccess() {
        // Given
        Authentication merchantAuth = new TestingAuthenticationToken(
            "merchant@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_MERCHANT"))
        );
        User ownUser = createTestUser(1L, "merchant@test.com", Role.MERCHANT);
        User otherUser = createTestUser(2L, "other@test.com", Role.CUSTOMER);

        // When & Then - Merchant has same access as customer
        assertTrue(permissionEvaluator.hasPermission(merchantAuth, ownUser, "READ"));
        assertTrue(permissionEvaluator.hasPermission(merchantAuth, ownUser, "WRITE"));
        assertTrue(permissionEvaluator.hasPermission(merchantAuth, ownUser, "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(merchantAuth, ownUser, "MANAGE"));

        assertTrue(permissionEvaluator.hasPermission(merchantAuth, otherUser, "READ"));
        assertFalse(permissionEvaluator.hasPermission(merchantAuth, otherUser, "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(merchantAuth, otherUser, "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(merchantAuth, otherUser, "MANAGE"));
    }

    @Test
    void testUserPermissionById() {
        // Given
        Authentication userAuth = new TestingAuthenticationToken(
            "user@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        User user = createTestUser(1L, "user@test.com", Role.CUSTOMER);
        
        when(userService.getUserById(1L)).thenReturn(user);

        // When & Then
        assertTrue(permissionEvaluator.hasPermission(userAuth, 1L, "user", "READ"));
        assertTrue(permissionEvaluator.hasPermission(userAuth, 1L, "user", "WRITE"));
        assertTrue(permissionEvaluator.hasPermission(userAuth, 1L, "user", "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 1L, "user", "MANAGE"));
    }

    @Test
    void testUserPermissionByIdOtherUser() {
        // Given
        Authentication userAuth = new TestingAuthenticationToken(
            "user@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        User otherUser = createTestUser(2L, "other@test.com", Role.CUSTOMER);
        
        when(userService.getUserById(2L)).thenReturn(otherUser);

        // When & Then - User cannot read other users' profiles (only own)
        assertFalse(permissionEvaluator.hasPermission(userAuth, 2L, "user", "READ"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 2L, "user", "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 2L, "user", "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 2L, "user", "MANAGE"));
    }

    @Test
    void testAddressPermission() {
        // Given
        Authentication userAuth = new TestingAuthenticationToken(
            "user@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        // When & Then - Address permissions require admin/moderator role
        assertFalse(permissionEvaluator.hasPermission(userAuth, 1L, "address", "READ"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 1L, "address", "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(userAuth, 1L, "address", "DELETE"));
    }

    @Test
    void testUnauthenticatedAccess() {
        // Given
        Authentication unauthenticated = new TestingAuthenticationToken(
            "anonymous",
            "password",
            Collections.emptyList()
        );
        // Set authenticated to false for unauthenticated users
        unauthenticated.setAuthenticated(false);
        User targetUser = createTestUser(1L, "user@test.com", Role.CUSTOMER);

        // When & Then
        assertFalse(permissionEvaluator.hasPermission(unauthenticated, targetUser, "READ"));
        assertFalse(permissionEvaluator.hasPermission(unauthenticated, targetUser, "WRITE"));
        assertFalse(permissionEvaluator.hasPermission(unauthenticated, targetUser, "DELETE"));
        assertFalse(permissionEvaluator.hasPermission(unauthenticated, targetUser, "MANAGE"));
    }

    @Test
    void testNullAuthentication() {
        // Given
        User targetUser = createTestUser(1L, "user@test.com", Role.CUSTOMER);

        // When & Then
        assertFalse(permissionEvaluator.hasPermission(null, targetUser, "READ"));
        assertFalse(permissionEvaluator.hasPermission(null, 1L, "user", "READ"));
    }

    @Test
    void testUnknownPermission() {
        // Given
        Authentication userAuth = new TestingAuthenticationToken(
            "user@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        User targetUser = createTestUser(1L, "user@test.com", Role.CUSTOMER);

        // When & Then
        assertFalse(permissionEvaluator.hasPermission(userAuth, targetUser, "UNKNOWN_PERMISSION"));
    }

    @Test
    void testUnknownTargetType() {
        // Given
        Authentication userAuth = new TestingAuthenticationToken(
            "user@test.com",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        // When & Then
        assertFalse(permissionEvaluator.hasPermission(userAuth, 1L, "unknown_type", "READ"));
    }

    // Helper methods
    private User createTestUser(Long id, String email, Role role) {
        User user = User.builder()
                .username("testuser")
                .email(email)
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(role)
                .emailVerified(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .build();
        
        // Use reflection to set the ID for testing purposes
        try {
            java.lang.reflect.Field idField = BaseVO.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID for test user", e);
        }
        
        return user;
    }
}
