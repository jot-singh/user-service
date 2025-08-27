package com.user.service.services.impl;

import com.user.service.dao.UserDao;
import com.user.service.dto.request.AddressRequestDto;
import com.user.service.dto.request.UserProfileRequestDto;
import com.user.service.dto.response.AddressResponseDto;
import com.user.service.dto.response.UserResponseDto;
import com.user.service.entity.Address;
import com.user.service.entity.Role;
import com.user.service.entity.User;
import com.user.service.error.UserNotFoundException;
import com.user.service.repository.AddressRepository;
import com.user.service.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.user.service.controller.AdminController.AdminStatsResponse;

/**
 * Implementation of UserService for user profile management
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Override
    public UserResponseDto getUserProfile(Long userId) {
        log.debug("Getting user profile for user ID: {}", userId);
        
        User user = getUserById(userId);
        return mapToUserResponseDto(user);
    }
    
    @Override
    public UserResponseDto updateUserProfile(Long userId, UserProfileRequestDto requestDto) {
        log.debug("Updating user profile for user ID: {} with data: {}", userId, requestDto);
        
        User user = getUserById(userId);
        
        // Update basic profile information
        if (requestDto.getFirstName() != null) {
            user.setFirstName(requestDto.getFirstName());
        }
        if (requestDto.getLastName() != null) {
            user.setLastName(requestDto.getLastName());
        }
        if (requestDto.getEmail() != null && !requestDto.getEmail().equals(user.getEmail())) {
            // TODO: Add email verification logic for email changes
            user.setEmail(requestDto.getEmail());
            user.setEmailVerified(false);
        }
        if (requestDto.getPhone() != null) {
            user.setPhone(requestDto.getPhone());
        }
        
        // Save updated user
        userDao.save(user);
        log.info("User profile updated successfully for user ID: {}", userId);
        
        return mapToUserResponseDto(user);
    }
    
    @Override
    public List<AddressResponseDto> getUserAddresses(Long userId) {
        log.debug("Getting addresses for user ID: {}", userId);
        
        // Verify user exists
        getUserById(userId);
        
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        return addresses.stream()
                .map(this::mapToAddressResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public AddressResponseDto getAddress(Long userId, Long addressId) {
        log.debug("Getting address ID: {} for user ID: {}", addressId, userId);
        
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or access denied"));
        
        return mapToAddressResponseDto(address);
    }
    
    @Override
    public AddressResponseDto createAddress(Long userId, AddressRequestDto requestDto) {
        log.debug("Creating new address for user ID: {}", userId);
        
        User user = getUserById(userId);
        
        // If this is the first address or marked as default, reset other default flags
        if (requestDto.getIsDefault() != null && requestDto.getIsDefault()) {
            addressRepository.resetDefaultFlagsByUserId(userId);
        }
        
        Address address = Address.builder()
                .user(user)
                .addressLine1(requestDto.getAddressLine1())
                .addressLine2(requestDto.getAddressLine2())
                .city(requestDto.getCity())
                .state(requestDto.getState())
                .postalCode(requestDto.getPostalCode())
                .country(requestDto.getCountry())
                .isDefault(requestDto.getIsDefault() != null ? requestDto.getIsDefault() : false)
                .label(requestDto.getLabel())
                .build();
        
        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully for user ID: {}", userId);
        
        return mapToAddressResponseDto(savedAddress);
    }
    
    @Override
    public AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto requestDto) {
        log.debug("Updating address ID: {} for user ID: {}", addressId, userId);
        
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or access denied"));
        
        // Update address fields
        if (requestDto.getAddressLine1() != null) {
            address.setAddressLine1(requestDto.getAddressLine1());
        }
        if (requestDto.getAddressLine2() != null) {
            address.setAddressLine2(requestDto.getAddressLine2());
        }
        if (requestDto.getCity() != null) {
            address.setCity(requestDto.getCity());
        }
        if (requestDto.getState() != null) {
            address.setState(requestDto.getState());
        }
        if (requestDto.getPostalCode() != null) {
            address.setPostalCode(requestDto.getPostalCode());
        }
        if (requestDto.getCountry() != null) {
            address.setCountry(requestDto.getCountry());
        }
        if (requestDto.getLabel() != null) {
            address.setLabel(requestDto.getLabel());
        }
        
        // Handle default address logic
        if (requestDto.getIsDefault() != null && requestDto.getIsDefault()) {
            addressRepository.resetDefaultFlagsByUserId(userId);
            address.setIsDefault(true);
        }
        
        Address savedAddress = addressRepository.save(address);
        log.info("Address updated successfully for user ID: {}", userId);
        
        return mapToAddressResponseDto(savedAddress);
    }
    
    @Override
    public void deleteAddress(Long userId, Long addressId) {
        log.debug("Deleting address ID: {} for user ID: {}", addressId, userId);
        
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or access denied"));
        
        addressRepository.delete(address);
        log.info("Address deleted successfully for user ID: {}", userId);
    }
    
    @Override
    public AddressResponseDto setDefaultAddress(Long userId, Long addressId) {
        log.debug("Setting default address ID: {} for user ID: {}", addressId, userId);
        
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found or access denied"));
        
        // Reset all default flags for this user by fetching and updating each address
        List<Address> userAddresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        for (Address userAddress : userAddresses) {
            if (userAddress.getIsDefault()) {
                userAddress.setIsDefault(false);
                addressRepository.save(userAddress);
            }
        }
        
        // Set this address as default
        address.setIsDefault(true);
        Address savedAddress = addressRepository.save(address);
        
        log.info("Default address set successfully for user ID: {}", userId);
        return mapToAddressResponseDto(savedAddress);
    }
    
    @Override
    public AddressResponseDto getDefaultAddress(Long userId) {
        log.debug("Getting default address for user ID: {}", userId);
        
        Address defaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new UserNotFoundException("No default address found for user"));
        
        return mapToAddressResponseDto(defaultAddress);
    }
    
    @Override
    public User getUserById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
    
    @Override
    public boolean isUserActive(Long userId) {
        try {
            User user = getUserById(userId);
            return user.isAccountActive();
        } catch (UserNotFoundException e) {
            return false;
        }
    }
    
    // Admin methods for role-based access control
    
    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.debug("Getting all users with pagination: {}", pageable);
        
        Page<User> users = userDao.findAll(pageable);
        return users.map(this::mapToUserResponseDto);
    }
    
    @Override
    public List<UserResponseDto> getUsersByRole(Role role) {
        log.debug("Getting users by role: {}", role);
        
        List<User> users = userDao.findByRole(role);
        return users.stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserResponseDto lockUserAccount(Long userId) {
        log.debug("Locking user account: {}", userId);
        
        User user = getUserById(userId);
        user.setAccountLocked(true);
        userDao.save(user);
        
        log.info("User account locked successfully: {}", userId);
        return mapToUserResponseDto(user);
    }
    
    @Override
    public UserResponseDto unlockUserAccount(Long userId) {
        log.debug("Unlocking user account: {}", userId);
        
        User user = getUserById(userId);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        userDao.save(user);
        
        log.info("User account unlocked successfully: {}", userId);
        return mapToUserResponseDto(user);
    }
    
    @Override
    public UserResponseDto changeUserRole(Long userId, Role newRole) {
        log.debug("Changing user role: {} to {}", userId, newRole);
        
        User user = getUserById(userId);
        user.setRole(newRole);
        userDao.save(user);
        
        log.info("User role changed successfully: {} to {}", userId, newRole);
        return mapToUserResponseDto(user);
    }
    
    @Override
    public void deleteUserAccount(Long userId) {
        log.debug("Deleting user account: {}", userId);
        
        User user = getUserById(userId);
        userDao.delete(user);
        
        log.info("User account deleted successfully: {}", userId);
    }
    
    @Override
    public AdminStatsResponse getSystemStatistics() {
        log.debug("Getting system statistics");
        
        long totalUsers = userDao.count();
        long activeUsers = userDao.countByAccountLockedFalseAndEmailVerifiedTrue();
        long lockedUsers = userDao.countByAccountLockedTrue();
        long customers = userDao.countByRole(Role.CUSTOMER);
        long merchants = userDao.countByRole(Role.MERCHANT);
        long admins = userDao.countByRole(Role.ADMIN);
        long moderators = userDao.countByRole(Role.MODERATOR);
        
        return new AdminStatsResponse(totalUsers, activeUsers, lockedUsers, customers, merchants, admins, moderators);
    }
    
    // Private helper methods
    
    private UserResponseDto mapToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getName())
                .phoneNumber(user.getPhone())
                .enabled(user.isAccountActive())
                .accountNonLocked(!user.getAccountLocked())
                .createdAt(user.getCreatedAt())
                .lastModifiedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLogin())
                .build();
    }
    
    private AddressResponseDto mapToAddressResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .label(address.getLabel())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .fullAddress(address.getFullAddress())
                .shortAddress(address.getShortAddress())
                .build();
    }
}
