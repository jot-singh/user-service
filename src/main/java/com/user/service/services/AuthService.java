package com.user.service.services;

import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.error.InvalidCredentialsException;
import com.user.service.error.UserAlreadyExistsException;


public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto) throws InvalidCredentialsException;

    AuthResponseDto signUp(AuthRequestDto authRequestDto) throws UserAlreadyExistsException;

    void logout(LogoutRequestDto logoutRequestDto);
}
