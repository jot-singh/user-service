package com.user.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating and updating user addresses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    
    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    private String addressLine1;
    
    @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    private String addressLine2;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "City can only contain letters, spaces, hyphens, and apostrophes")
    private String city;
    
    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State/Province cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "State/Province can only contain letters, spaces, hyphens, and apostrophes")
    private String state;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Postal code can only contain letters, numbers, spaces, and hyphens")
    private String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "Country can only contain letters, spaces, hyphens, and apostrophes")
    private String country;
    
    private Boolean isDefault = false;
    
    @Size(max = 100, message = "Address label cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Address label can only contain letters, numbers, spaces, and hyphens")
    private String label; // e.g., "Home", "Work", "Shipping"
}
