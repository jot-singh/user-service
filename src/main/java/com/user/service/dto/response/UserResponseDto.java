package com.user.service.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user information responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    @NotBlank(message = "User ID is required")
    @Size(max = 50, message = "User ID must be less than 50 characters")
    private String id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;
    
    @Pattern(
        regexp = "^(USER|ADMIN|MODERATOR)$",
        message = "Role must be one of: USER, ADMIN, MODERATOR"
    )
    private String role;
    
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;
    
    private boolean enabled;
    private boolean accountNonLocked;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime lastLoginAt;
}
