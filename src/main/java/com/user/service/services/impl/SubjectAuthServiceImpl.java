package com.user.service.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public SubjectAuthServiceImpl(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) throws InvalidCredentialsException {
        Optional<User> user = userDao.findByUsername(authRequestDto.getUsername());
        if (user.isEmpty()|| !user.get().getPassword().equals(authRequestDto.getPassword())) {
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        String token = JwtTokenUtil.generateToken(user.get().getUsername());
        Session session = new Session();
        session.setToken(token);
        session.setUsername(user.get().getUsername());
        session.setSessionId(UUID.randomUUID().toString());
        sessionDao.save(session);
        AuthResponseDto authResponseDto = AuthResponseDto.builder().build();
        authResponseDto.setToken(token);
        authResponseDto.setData(session);
        return authResponseDto;
    }

    @Override
    public AuthResponseDto signUp(AuthRequestDto authRequestDto) throws UserAlreadyExistsException {
        Optional<User> user = userDao.findByUsername(authRequestDto.getUsername());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User exists");
        }
        User newUser = User.builder()
                .username(authRequestDto.getUsername())
                .password(authRequestDto.getPassword())
                .email(authRequestDto.getEmail())
                .role(authRequestDto.getRole() != null ? Role.valueOf(authRequestDto.getRole()) : Role.USER)
                .build();
        userDao.save(newUser);
        return AuthResponseDto.builder().id(newUser.getEmail())
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

    @Override
    public void validateToken(String token) throws InvalidCredentialsException {
        JwtTokenUtil.validateToken(token);
    }

    @Override
    public BaseResponseDto updateUser(String userId, AuthRequestDto authRequestDto) throws UsernameNotFoundException {
        Optional<User> user = userDao.findById(Long.parseLong(userId));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        user.get().setPassword(authRequestDto.getPassword());
        user.get().setEmail(authRequestDto.getEmail());
        user.get().setRole(Role.valueOf(authRequestDto.getRole()));
        userDao.save(user.get());
        return BaseResponseDto.builder()
                .id(user.get().getUsername())
                .message("User updated successfully")
                .build();
    }
}
