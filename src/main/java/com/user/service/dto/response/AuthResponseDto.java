package com.user.service.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO for authentication responses
 */
@SuperBuilder
@Getter(value = AccessLevel.PROTECTED)
@Setter
public class AuthResponseDto extends BaseResponseDto {
    
    @NotBlank(message = "Token is required")
    @Size(min = 10, max = 5000, message = "Token must be between 10 and 5000 characters")
    private String token;
}
