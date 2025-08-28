package com.user.service.dto.request;

import com.user.service.entity.Role;
import com.user.service.validation.annotations.StrongPassword;
import com.user.service.validation.annotations.ValidUsername;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration requests
 * Includes comprehensive validation for secure user creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @ValidUsername(message = "Username contains invalid characters or is reserved")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @StrongPassword(message = "Password must meet security requirements")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Phone number can only contain digits, spaces, hyphens, parentheses, and plus sign")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Builder.Default
    private Role role = Role.CUSTOMER;

    @AssertTrue(message = "Password confirmation must match password")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    @AssertTrue(message = "Terms and conditions must be accepted")
    @Builder.Default
    private Boolean acceptTerms = false;

    @AssertTrue(message = "Privacy policy must be accepted")
    @Builder.Default
    private Boolean acceptPrivacyPolicy = false;

    @Builder.Default
    private Boolean sendWelcomeEmail = true;

    @Builder.Default
    private Boolean requireEmailVerification = true;
}
