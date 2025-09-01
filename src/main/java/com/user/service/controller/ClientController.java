package com.user.service.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.web.bind.annotation.*;

import com.user.service.dto.ClientRegistrationDto;
import com.user.service.dto.ClientResponseDto;
import com.user.service.security.models.Client;
import com.user.service.security.repositories.ClientRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new OAuth2 client
     */
    @PostMapping("/register")
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientRegistrationDto request) {
        try {
            // Generate client credentials
            String clientId = request.getClientId() != null ? request.getClientId() : "client-" + UUID.randomUUID().toString();
            String clientSecret = UUID.randomUUID().toString();
            String encodedClientSecret = passwordEncoder.encode(clientSecret);

            // Build RegisteredClient
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(encodedClientSecret)
                    .clientName(request.getClientName())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUris(uris -> {
                        if (request.getRedirectUris() != null && !request.getRedirectUris().isEmpty()) {
                            uris.addAll(request.getRedirectUris());
                        }
                    })
                    .scopes(scopes -> {
                        scopes.add("read");
                        scopes.add("write");
                        if (request.getScopes() != null && !request.getScopes().isEmpty()) {
                            scopes.addAll(request.getScopes());
                        }
                    })
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .tokenSettings(TokenSettings.builder()
                            .build())
                    .build();

            // Save to repository
            registeredClientRepository.save(registeredClient);

            // Prepare response (don't include encoded secret)
            ClientResponseDto response = new ClientResponseDto();
            response.setClientId(clientId);
            response.setClientSecret(clientSecret); // Return plain secret for initial setup
            response.setClientName(request.getClientName());
            response.setScopes(List.of(registeredClient.getScopes().toArray(new String[0])));
            response.setRedirectUris(request.getRedirectUris());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get client information by clientId
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable String clientId) {
        try {
            RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
            if (registeredClient == null) {
                return ResponseEntity.notFound().build();
            }

            ClientResponseDto response = new ClientResponseDto();
            response.setClientId(registeredClient.getClientId());
            response.setClientName(registeredClient.getClientName());
            response.setScopes(List.of(registeredClient.getScopes().toArray(new String[0])));
            response.setRedirectUris(List.of(registeredClient.getRedirectUris().toArray(new String[0])));
            // Don't return client secret for security

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * List all registered clients (admin endpoint)
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            List<ClientResponseDto> responses = clients.stream()
                    .map(client -> {
                        ClientResponseDto response = new ClientResponseDto();
                        response.setClientId(client.getClientId());
                        response.setClientName(client.getClientName());
                        // Parse scopes and redirect URIs if needed
                        return response;
                    })
                    .toList();

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a client registration
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        try {
            Optional<Client> client = clientRepository.findByClientId(clientId);
            if (client.isPresent()) {
                clientRepository.delete(client.get());
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create initial Product Service client for testing
     */
    @PostMapping("/bootstrap/product-service")
    public ResponseEntity<ClientResponseDto> createProductServiceClient() {
        try {
            String clientId = "product-service";
            String clientSecret = "product-service-secret";
            String encodedClientSecret = passwordEncoder.encode(clientSecret);

            // Check if client already exists
            RegisteredClient existingClient = registeredClientRepository.findByClientId(clientId);
            if (existingClient != null) {
                // Client already exists, return current configuration
                ClientResponseDto response = new ClientResponseDto();
                response.setClientId(clientId);
                response.setClientSecret(clientSecret);
                response.setClientName("Product Service");
                response.setScopes(List.of("user.read", "user.validate", "read", "write", "openid", "profile"));
                response.setRedirectUris(List.of("http://localhost:3000/callback"));
                return ResponseEntity.ok(response);
            }

            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret(encodedClientSecret)
                    .clientName("Product Service")
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUris(uris -> {
                        uris.add("http://localhost:3000/callback");
                    })
                    .scopes(scopes -> {
                        scopes.add("user.read");
                        scopes.add("user.validate");
                        scopes.add("read");
                        scopes.add("write");
                        scopes.add("openid");
                        scopes.add("profile");
                    })
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(true)
                            .build())
                    .tokenSettings(TokenSettings.builder()
                            .build())
                    .build();

            registeredClientRepository.save(registeredClient);

            ClientResponseDto response = new ClientResponseDto();
            response.setClientId(clientId);
            response.setClientSecret(clientSecret);
            response.setClientName("Product Service");
            response.setScopes(List.of("user.read", "user.validate", "read", "write", "openid", "profile"));
            response.setRedirectUris(List.of("http://localhost:3000/callback"));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
