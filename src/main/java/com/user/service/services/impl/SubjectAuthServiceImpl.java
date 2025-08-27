package com.user.service.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.service.dao.SessionDao;
import com.user.service.dao.UserDao;
import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.dto.response.BaseResponseDto;
import com.user.service.entity.Role;
import com.user.service.entity.Session;
import com.user.service.entity.User;
import com.user.service.error.InvalidCredentialsException;
import com.user.service.error.UserAlreadyExistsException;
import com.user.service.services.AuthService;
import com.user.service.util.JwtTokenUtil;

@Service
public class SubjectAuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public SubjectAuthServiceImpl(UserDao userDao, SessionDao sessionDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) throws InvalidCredentialsException {
        Optional<User> userOptional = userDao.findByUsername(authRequestDto.getUsername());
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        
        User user = userOptional.get();
        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        
        String token = JwtTokenUtil.generateToken(user.getUsername());
        Session session = new Session();
        session.setToken(token);
        session.setUsername(user.getUsername());
        session.setSessionId(UUID.randomUUID().toString());
        sessionDao.save(session);
        
        AuthResponseDto authResponseDto = AuthResponseDto.builder().build();
        authResponseDto.setToken(token);
        authResponseDto.setData(session);
        return authResponseDto;
    }

    @Override
    public AuthResponseDto signUp(AuthRequestDto authRequestDto) throws UserAlreadyExistsException {
        Optional<User> existingUser = userDao.findByUsername(authRequestDto.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User exists");
        }
        
        // Validate password strength
        if (!isValidPassword(authRequestDto.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number");
        }
        
        User newUser = User.builder()
                .username(authRequestDto.getUsername())
                .password(passwordEncoder.encode(authRequestDto.getPassword())) // Encode password
                .email(authRequestDto.getEmail())
                .role(authRequestDto.getRole() != null ? Role.valueOf(authRequestDto.getRole()) : Role.USER)
                .build();
        userDao.save(newUser);
        
        return AuthResponseDto.builder()
                .id(newUser.getEmail())
                .message("User Account is created successfully")
                .build();
    }

    @Override
    public void logout(LogoutRequestDto logoutRequestDto) {
        if (validUsername(logoutRequestDto.getUsername()) && validJWTToken(logoutRequestDto.getToken())) {
            Session session = sessionDao.findByTokenAndUsername(logoutRequestDto.getToken(),
                    logoutRequestDto.getUsername());
            if (session != null) {
                sessionDao.delete(session);
            }
        }

    }

    private boolean validUsername(String username) {
        return username != null && !username.isEmpty();
    }

    private boolean validJWTToken(String token) {
        try {
            validateToken(token);
            return true;
        } catch (InvalidCredentialsException e) {
            return false;
        }
    }

    /**
     * Validates password strength
     * Requirements: At least 8 characters, one uppercase, one lowercase, one number
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        
        return hasUpper && hasLower && hasDigit;
    }

    @Override
    public void validateToken(String token) throws InvalidCredentialsException {
        JwtTokenUtil.validateToken(token);
    }

    @Override
    public BaseResponseDto updateUser(String userId, AuthRequestDto authRequestDto) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.findById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        
        User user = userOptional.get();
        
        // Update password only if provided and validate strength
        if (authRequestDto.getPassword() != null && !authRequestDto.getPassword().trim().isEmpty()) {
            if (!isValidPassword(authRequestDto.getPassword())) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number");
            }
            user.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        }
        
        // Update other fields
        if (authRequestDto.getEmail() != null && !authRequestDto.getEmail().trim().isEmpty()) {
            user.setEmail(authRequestDto.getEmail());
        }
        
        if (authRequestDto.getRole() != null && !authRequestDto.getRole().trim().isEmpty()) {
            user.setRole(Role.valueOf(authRequestDto.getRole()));
        }
        
        userDao.save(user);
        
        return BaseResponseDto.builder()
                .id(user.getUsername())
                .message("User updated successfully")
                .build();
    }
}
