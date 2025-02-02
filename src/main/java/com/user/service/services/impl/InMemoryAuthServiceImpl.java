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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.SecretKey;

@Service
public class InMemoryAuthServiceImpl implements AuthService {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private SecretKey key = Keys.hmacShaKeyFor(
        "namanisveryveryveryveryveryveryverycool"
                .getBytes(StandardCharsets.UTF_8));;

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
        try {
            validateToken(token);
            return true;
        } catch (InvalidCredentialsException e) {
            return false;
        }
    }

    @Override
    public void validateToken(String token) throws InvalidCredentialsException {
        Objects.requireNonNull(token, "Token is required");
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Date expiryAt = claims.getPayload().getExpiration();
            Date now = new Date();
            if (expiryAt.before(now)) {
                throw new InvalidCredentialsException("Token expired");
            }
            Long userId = claims.getPayload().get("user_id", Long.class);
            if (userId == null) {
                throw new InvalidCredentialsException("Invalid token");
            }
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid token");
        }
    }
}
