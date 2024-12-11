package com.user.service.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDto {
    String username;
    String email;
    String password;
}
