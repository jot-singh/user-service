package com.user.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for logout requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDto {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Token is required")
    @Size(min = 10, message = "Token must be at least 10 characters")
    private String token;
}
