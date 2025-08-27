package com.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.dto.request.AddressRequestDto;
import com.user.service.dto.request.UserProfileRequestDto;
import com.user.service.dto.response.AddressResponseDto;
import com.user.service.dto.response.UserResponseDto;
import com.user.service.entity.Address;
import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.repository.AddressRepository;
import com.user.service.repository.UserRepository;
import com.user.service.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for User Profile Management functionality
 * Tests user profile CRUD operations and address management
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class UserProfileManagementTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    private User testUser;
    private Address testAddress;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .emailVerified(true)
                .accountLocked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testUser = userRepository.save(testUser);
        
        // Create test address
        testAddress = Address.builder()
                .user(testUser)
                .addressLine1("123 Test Street")
                .addressLine2("Apt 4B")
                .city("Test City")
                .state("Test State")
                .postalCode("12345")
                .country("Test Country")
                .isDefault(true)
                .label("Home")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testAddress = addressRepository.save(testAddress);
    }
    
    // User Profile Tests
    
    @Test
    void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/users/{userId}/profile", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.phoneNumber").value("+1234567890"));
    }
    
    @Test
    void testUpdateUserProfile() throws Exception {
        UserProfileRequestDto updateRequest = UserProfileRequestDto.builder()
                .firstName("Updated")
                .lastName("Name")
                .phone("+0987654321")
                .languagePreference("en-US")
                .currencyPreference("USD")
                .timezone("America/New_York")
                .marketingEmails(true)
                .orderNotifications(true)
                .securityAlerts(false)
                .build();
        
        mockMvc.perform(put("/users/{userId}/profile", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+0987654321"));
        
        // Verify database update
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("+0987654321", updatedUser.getPhone());
    }
    
    @Test
    void testUpdateUserProfileWithInvalidData() throws Exception {
        UserProfileRequestDto invalidRequest = UserProfileRequestDto.builder()
                .firstName("") // Invalid: empty string
                .phone("invalid-phone") // Invalid: wrong format
                .currencyPreference("INVALID") // Invalid: not 3 letters
                .build();
        
        mockMvc.perform(put("/users/{userId}/profile", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    // Address Management Tests
    
    @Test
    void testGetUserAddresses() throws Exception {
        mockMvc.perform(get("/users/{userId}/addresses", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testAddress.getId()))
                .andExpect(jsonPath("$[0].addressLine1").value("123 Test Street"))
                .andExpect(jsonPath("$[0].city").value("Test City"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[0].label").value("Home"));
    }
    
    @Test
    void testGetSpecificAddress() throws Exception {
        mockMvc.perform(get("/users/{userId}/addresses/{addressId}", testUser.getId(), testAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddress.getId()))
                .andExpect(jsonPath("$.fullAddress").exists())
                .andExpect(jsonPath("$.shortAddress").exists());
    }
    
    @Test
    void testCreateAddress() throws Exception {
        AddressRequestDto newAddress = AddressRequestDto.builder()
                .addressLine1("456 New Street")
                .city("New City")
                .state("New State")
                .postalCode("54321")
                .country("New Country")
                .isDefault(false)
                .label("Work")
                .build();
        
        mockMvc.perform(post("/users/{userId}/addresses", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAddress)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.addressLine1").value("456 New Street"))
                .andExpect(jsonPath("$.city").value("New City"))
                .andExpect(jsonPath("$.isDefault").value(false))
                .andExpect(jsonPath("$.label").value("Work"));
        
        // Verify database creation
        assertEquals(2, addressRepository.countByUserId(testUser.getId()));
    }
    
    @Test
    void testCreateAddressWithInvalidData() throws Exception {
        AddressRequestDto invalidAddress = AddressRequestDto.builder()
                .addressLine1("") // Invalid: empty string
                .city("Test City")
                .state("Test State")
                .postalCode("12345")
                .country("Test Country")
                .build();
        
        mockMvc.perform(post("/users/{userId}/addresses", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAddress)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testUpdateAddress() throws Exception {
        AddressRequestDto updateRequest = AddressRequestDto.builder()
                .addressLine1("Updated Street")
                .city("Updated City")
                .state("Updated State")
                .postalCode("99999")
                .country("Updated Country")
                .label("Updated Label")
                .build();
        
        mockMvc.perform(put("/users/{userId}/addresses/{addressId}", testUser.getId(), testAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressLine1").value("Updated Street"))
                .andExpect(jsonPath("$.city").value("Updated City"))
                .andExpect(jsonPath("$.label").value("Updated Label"));
        
        // Verify database update
        Address updatedAddress = addressRepository.findById(testAddress.getId()).orElseThrow();
        assertEquals("Updated Street", updatedAddress.getAddressLine1());
        assertEquals("Updated City", updatedAddress.getCity());
    }
    
    @Test
    void testDeleteAddress() throws Exception {
        mockMvc.perform(delete("/users/{userId}/addresses/{addressId}", testUser.getId(), testAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verify database deletion
        assertFalse(addressRepository.existsById(testAddress.getId()));
        assertEquals(0, addressRepository.countByUserId(testUser.getId()));
    }
    
    @Test
    void testSetDefaultAddress() throws Exception {
        // Create a second address
        Address secondAddress = Address.builder()
                .user(testUser)
                .addressLine1("789 Second Street")
                .city("Second City")
                .state("Second State")
                .postalCode("67890")
                .country("Second Country")
                .isDefault(false)
                .label("Office")
                .build();
        secondAddress = addressRepository.save(secondAddress);
        
        // Set second address as default
        mockMvc.perform(put("/users/{userId}/addresses/{addressId}/default", testUser.getId(), secondAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
        
        // Verify first address is no longer default
        Address firstAddress = addressRepository.findById(testAddress.getId()).orElseThrow();
        assertFalse(firstAddress.getIsDefault());
        
        // Verify second address is now default
        Address newDefaultAddress = addressRepository.findById(secondAddress.getId()).orElseThrow();
        assertTrue(newDefaultAddress.getIsDefault());
    }
    
    @Test
    void testGetDefaultAddress() throws Exception {
        mockMvc.perform(get("/users/{userId}/addresses/default", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddress.getId()))
                .andExpect(jsonPath("$.isDefault").value(true));
    }
    
    // Security Tests
    
    @Test
    void testAccessDeniedForOtherUserAddress() throws Exception {
        // Create another user
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .emailVerified(true)
                .accountLocked(false)
                .build();
        otherUser = userRepository.save(otherUser);
        
        // Try to access other user's address
        mockMvc.perform(get("/users/{userId}/addresses/{addressId}", otherUser.getId(), testAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Should return 404, not 403
    }
    
    @Test
    void testUpdateOtherUserAddress() throws Exception {
        User otherUser = User.builder()
                .username("otheruser")
                .email("other@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .emailVerified(true)
                .accountLocked(false)
                .build();
        otherUser = userRepository.save(otherUser);
        
        AddressRequestDto updateRequest = AddressRequestDto.builder()
                .addressLine1("Hacked Street")
                .city("Hacked City")
                .state("Hacked State")
                .postalCode("00000")
                .country("Hacked Country")
                .build();
        
        mockMvc.perform(put("/users/{userId}/addresses/{addressId}", otherUser.getId(), testAddress.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()); // Should return 404, not 403
    }
    
    // Business Logic Tests
    
    @Test
    void testAddressBusinessLogic() {
        Address address = addressRepository.findById(testAddress.getId()).orElseThrow();
        
        // Test full address formatting
        String fullAddress = address.getFullAddress();
        assertTrue(fullAddress.contains("123 Test Street"));
        assertTrue(fullAddress.contains("Test City"));
        assertTrue(fullAddress.contains("Test State"));
        assertTrue(fullAddress.contains("12345"));
        assertTrue(fullAddress.contains("Test Country"));
        
        // Test short address formatting
        String shortAddress = address.getShortAddress();
        assertEquals("Test City, Test State, Test Country", shortAddress);
        
        // Test address type checks
        assertTrue(address.isBillingAddress()); // Default address is billing
        assertFalse(address.isShippingAddress()); // Default address is not shipping
    }
    
    @Test
    void testUserProfileBusinessLogic() {
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        
        // Test full name
        assertEquals("Test User", user.getFullName());
        
        // Test account status
        assertTrue(user.isAccountActive());
        
        // Test failed login attempts
        user.incrementFailedAttempts();
        assertEquals(1, user.getFailedLoginAttempts());
        assertFalse(user.getAccountLocked());
        
        // Test account lock after 5 failed attempts
        for (int i = 0; i < 4; i++) {
            user.incrementFailedAttempts();
        }
        assertTrue(user.getAccountLocked());
        
        // Test reset failed attempts
        user.resetFailedAttempts();
        assertEquals(0, user.getFailedLoginAttempts());
        assertFalse(user.getAccountLocked());
    }
}
