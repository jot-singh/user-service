package com.user.service.services.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import com.user.service.error.UserNotFoundException;
import com.user.service.services.AuthService;
import com.user.service.util.JwtTokenUtil;

@Service
public class SubjectAuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${session.concurrent.max:1}")
    private int maxConcurrentSessions;

    public SubjectAuthServiceImpl(UserDao userDao, SessionDao sessionDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        Optional<User> userOptional = userDao.findByUsername(authRequestDto.getUsername());
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        
        User user = userOptional.get();
        
        // Check if account is locked
        if (user.getAccountLocked()) {
            throw new InvalidCredentialsException("Account is locked due to multiple failed login attempts. Please contact support.");
        }
        
        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new InvalidCredentialsException("Please verify your email address before logging in.");
        }
        
        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            // Increment failed attempts
            user.incrementFailedAttempts();
            userDao.save(user);
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        
        // Successful login - reset failed attempts and update last login
        user.resetFailedAttempts();
        user.updateLastLogin();
        userDao.save(user);

        // Enforce concurrent session control
        List<Session> activeSessions = sessionDao.findByUsername(user.getUsername());
        if (activeSessions.size() >= maxConcurrentSessions) {
            activeSessions.stream()
                    .min(Comparator.comparing(Session::getCreatedAt))
                    .ifPresent(sessionDao::delete);
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
    public AuthResponseDto signUp(AuthRequestDto authRequestDto) {
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
                .role(authRequestDto.getRole() != null ? Role.valueOf(authRequestDto.getRole()) : Role.CUSTOMER)
                .build();
        userDao.save(newUser);

        // Generate token for immediate login after signup
        String token = JwtTokenUtil.generateToken(newUser.getUsername());
        Session session = new Session();
        session.setToken(token);
        session.setUsername(newUser.getUsername());
        session.setSessionId(UUID.randomUUID().toString());
        sessionDao.save(session);

        return AuthResponseDto.builder()
                .token(token)
                .data(session)
                .id(newUser.getEmail())
                .message("User account created successfully and logged in")
                .build();
    }

    @Override
    public void logout(LogoutRequestDto logoutRequestDto) {
        if (validUsername(logoutRequestDto.getUsername()) && validJWTToken(logoutRequestDto.getToken())) {
            sessionDao.findByTokenAndUsername(logoutRequestDto.getToken(),
                    logoutRequestDto.getUsername()).ifPresent(sessionDao::delete);
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
    public void validateToken(String token) {
        JwtTokenUtil.validateToken(token);
    }

    @Override
    public BaseResponseDto updateUser(String userId, AuthRequestDto authRequestDto) {
        Optional<User> userOptional = userDao.findById(Long.parseLong(userId));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
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
