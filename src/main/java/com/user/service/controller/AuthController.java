package com.user.service.controller;

//Write a controller class with name AuthController inside controller package. This class should have below functions.
//login(AuthRequestDto) - should return the jwt token for the user
//signUp(AuthRequestDto) - should create user and return the created user
//logout() - should logout the user

import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.error.InvalidCredentialsException;
import com.user.service.error.UserAlreadyExistsException;
import com.user.service.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) throws InvalidCredentialsException {
        return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponseDto> signUp(@RequestBody AuthRequestDto authRequestDto) throws UserAlreadyExistsException {
        return ResponseEntity.ok(authService.signUp(authRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        authService.logout(logoutRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestBody String token) throws InvalidCredentialsException {
        authService.validateToken(token);
        return ResponseEntity.ok().build();
    }
    
}
