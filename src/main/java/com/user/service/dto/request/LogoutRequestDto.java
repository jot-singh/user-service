package com.user.service.dto.request;

public class LogoutRequestDto {
    private final String username;
    private final String token;

    public LogoutRequestDto(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
