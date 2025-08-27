package com.user.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    @Size(max = 100, message = "First name must be less than 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    private String firstName;
    
    @Size(max = 100, message = "Last name must be less than 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    private String lastName;
    
    @Pattern(
        regexp = "^(USER|ADMIN|MODERATOR)$",
        message = "Role must be one of: USER, ADMIN, MODERATOR"
    )
    private String role;
    
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Phone number can only contain digits, spaces, hyphens, parentheses, and plus sign")
    private String phoneNumber;
}
