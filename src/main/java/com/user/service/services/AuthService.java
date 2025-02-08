package com.user.service.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.dto.response.BaseResponseDto;
import com.user.service.error.InvalidCredentialsException;
import com.user.service.error.UserAlreadyExistsException;


public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto) throws InvalidCredentialsException;

    BaseResponseDto signUp(AuthRequestDto authRequestDto) throws UserAlreadyExistsException;

    void logout(LogoutRequestDto logoutRequestDto);

    void validateToken(String token) throws InvalidCredentialsException;

    BaseResponseDto updateUser(String userId, AuthRequestDto authRequestDto) throws UsernameNotFoundException;
}
