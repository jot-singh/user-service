package com.user.service.controller;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Write a controller class with name AuthController inside controller package. This class should have below functions.
//login(AuthRequestDto) - should return the jwt token for the user
//signUp(AuthRequestDto) - should create user and return the created user
//logout() - should logout the user

import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.dto.response.BaseResponseDto;
import com.user.service.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponseDto> signUp(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.signUp(authRequestDto));
    }

    //Update User Details
    @PutMapping("/update/{userId}")
    public ResponseEntity<BaseResponseDto> updateUser(@RequestParam String userId, @Valid @RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(authService.updateUser(userId,authRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logout(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        authService.logout(logoutRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        Objects.requireNonNull(request.getHeader("Authorization"), "Token is required");
        String token = request.getHeader("Authorization").substring(7);
        authService.validateToken(token);
        return ResponseEntity.ok().build();
    }
    
}
