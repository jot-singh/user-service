package com.user.service.services.impl;

import com.user.service.dao.SessionDao;
import com.user.service.dao.UserDao;
import com.user.service.dto.request.AuthRequestDto;
import com.user.service.dto.request.LogoutRequestDto;
import com.user.service.dto.response.AuthResponseDto;
import com.user.service.entity.Session;
import com.user.service.entity.User;
import com.user.service.error.InvalidCredentialsException;
import com.user.service.error.UserAlreadyExistsException;
import com.user.service.services.AuthService;
import com.user.service.util.JwtTokenUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InMemoryAuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private final SessionDao sessionDao;

    public InMemoryAuthServiceImpl(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) throws InvalidCredentialsException {
        User user = userDao.findByUsername(authRequestDto.getUsername());
        if (user == null || !user.getPassword().equals(authRequestDto.getPassword())) {
            throw new InvalidCredentialsException("Provided Credentials are invalid");
        }
        String token = JwtTokenUtil.generateToken(user.getUsername().concat(user.getEmail()));
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
        User user = userDao.findByUsername(authRequestDto.getUsername());
        if (user != null) {
            throw new UserAlreadyExistsException("User exists");
        }
        user = User.builder()
                .username(authRequestDto.getUsername())
                .password(authRequestDto.getPassword())
                .email(authRequestDto.getEmail())
                .build();
        userDao.save(user);
        return AuthResponseDto.builder().id(user.getEmail())
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
        return token != null && !token.isEmpty();
    }
}
