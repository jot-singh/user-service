package com.user.service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientRegistrationDto {
    
    @Size(max = 50, message = "Client ID must be less than 50 characters")
    private String clientId;
    
    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name must be less than 100 characters")
    private String clientName;
    
    private List<String> redirectUris;
    
    private List<String> scopes;
    
    // Constructors
    public ClientRegistrationDto() {}
    
    public ClientRegistrationDto(String clientId, String clientName, List<String> redirectUris, List<String> scopes) {
        this.clientId = clientId;
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
