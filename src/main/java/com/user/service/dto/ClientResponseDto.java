package com.user.service.dto;

import java.util.List;

public class ClientResponseDto {
    
    private String clientId;
    private String clientSecret; // Only returned during registration
    private String clientName;
    private List<String> redirectUris;
    private List<String> scopes;
    
    // Constructors
    public ClientResponseDto() {}
    
    public ClientResponseDto(String clientId, String clientSecret, String clientName, 
                           List<String> redirectUris, List<String> scopes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientName = clientName;
        this.redirectUris = redirectUris;
        this.scopes = scopes;
    }
    
    // Getters and Setters
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public List<String> getRedirectUris() {
        return redirectUris;
    }
    
    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }
    
    public List<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
