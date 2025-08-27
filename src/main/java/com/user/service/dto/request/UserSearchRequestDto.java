package com.user.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user search requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequestDto {
    
    @Size(max = 50, message = "Username search term must be less than 50 characters")
    private String username;
    
    @Email(message = "Email search term must be a valid email address")
    @Size(max = 100, message = "Email search term must be less than 100 characters")
    private String email;
    
    @Size(max = 100, message = "First name search term must be less than 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Last name search term must be less than 100 characters")
    private String lastName;
    
    @Pattern(
        regexp = "^(USER|ADMIN|MODERATOR)$",
        message = "Role filter must be one of: USER, ADMIN, MODERATOR"
    )
    private String role;
    
    private Boolean enabled;
    private Boolean accountNonLocked;
    
    @Min(value = 1, message = "Page number must be at least 1")
    @Max(value = 1000, message = "Page number cannot exceed 1000")
    private Integer page = 1;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;
    
    @Pattern(
        regexp = "^(username|email|firstName|lastName|role|createdAt|lastModifiedAt)$",
        message = "Sort field must be one of: username, email, firstName, lastName, role, createdAt, lastModifiedAt"
    )
    private String sortBy = "username";
    
    @Pattern(
        regexp = "^(asc|desc)$",
        message = "Sort direction must be either 'asc' or 'desc'"
    )
    private String sortDirection = "asc";
}
