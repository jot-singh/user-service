package com.user.service.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base response DTO with common fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseResponseDto {
    
    @Size(max = 50, message = "ID must be less than 50 characters")
    protected String id;
    
    @Size(max = 500, message = "Message must be less than 500 characters")
    protected String message;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SUCCESS|ERROR|WARNING|INFO)$", message = "Status must be one of: SUCCESS, ERROR, WARNING, INFO")
    protected String status;
    
    protected Object data;
}
