package com.user.service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter(value = AccessLevel.PROTECTED)
@Setter
public class AuthResponseDto extends BaseResponseDto {
    String token;
}
