package com.user.service.dto.request;

import com.user.service.validation.annotations.StrongPassword;
import com.user.service.validation.annotations.ValidUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication requests (login/signup)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    
    @NotBlank(message = "Username is required")
    @ValidUsername(minLength = 3, maxLength = 50, allowReservedNames = false)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @StrongPassword(minLength = 8, maxLength = 128)
    private String password;
    
    @Pattern(
        regexp = "^(USER|ADMIN|MODERATOR)$",
        message = "Role must be one of: USER, ADMIN, MODERATOR"
    )
    private String role = "USER"; // Default role
}
