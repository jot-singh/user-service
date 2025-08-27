package com.user.service.services;

import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.dto.response.BaseResponseDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto);

    AuthResponseDto signUp(AuthRequestDto authRequestDto);

    void logout(LogoutRequestDto logoutRequestDto);

    void validateToken(String token);

    BaseResponseDto updateUser(String userId, AuthRequestDto authRequestDto);
}
