package com.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OAuth2 client responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    
    @NotBlank(message = "Client ID is required")
    @Size(max = 50, message = "Client ID must be less than 50 characters")
    private String clientId;
    
    @Size(max = 255, message = "Client secret must be less than 255 characters")
    private String clientSecret; // Only returned during registration
    
    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name must be less than 100 characters")
    private String clientName;
    
    private List<@Size(max = 500, message = "Redirect URI must be less than 500 characters") String> redirectUris;
    
    private List<@Size(max = 100, message = "Scope must be less than 100 characters") String> scopes;
}
