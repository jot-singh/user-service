package com.user.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDto {
    
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;
    
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;
    
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;
    
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Phone number can only contain digits, spaces, hyphens, parentheses, and plus sign")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;
    
    // User preferences
    @Size(max = 100, message = "Language preference cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\-]+$", message = "Language preference can only contain letters and hyphens")
    private String languagePreference;
    
    @Size(max = 100, message = "Currency preference cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency preference must be a 3-letter currency code")
    private String currencyPreference;
    
    @Size(max = 100, message = "Timezone cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z_/]+$", message = "Timezone can only contain letters, underscores, and forward slashes")
    private String timezone;
    
    private Boolean marketingEmails;
    private Boolean orderNotifications;
    private Boolean securityAlerts;
}
