package com.user.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password change requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDto {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "New password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
