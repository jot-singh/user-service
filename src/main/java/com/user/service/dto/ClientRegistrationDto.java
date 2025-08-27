package com.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for OAuth2 client registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationDto {
    
    @Size(max = 50, message = "Client ID must be less than 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Client ID can only contain letters, numbers, hyphens, and underscores")
    private String clientId;
    
    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name must be less than 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s_-]+$", message = "Client name can only contain letters, numbers, spaces, hyphens, and underscores")
    private String clientName;
    
    private List<@Pattern(
        regexp = "^https?://[\\w\\d\\-._~:/?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=.]+$",
        message = "Redirect URI must be a valid HTTP/HTTPS URL"
    ) String> redirectUris;
    
    private List<@Pattern(
        regexp = "^[a-z][a-z0-9._-]*$",
        message = "Scope must start with a letter and contain only lowercase letters, numbers, dots, underscores, and hyphens"
    ) String> scopes;
}
