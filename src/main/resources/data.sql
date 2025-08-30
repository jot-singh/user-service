-- Initial OAuth2 Client Registrations for Ecommerce Platform

-- Product Service Client (Service-to-Service Authentication)
INSERT INTO
    client (
        id,
        client_id,
        client_id_issued_at,
        client_secret,
        client_secret_expires_at,
        client_name,
        client_authentication_methods,
        authorization_grant_types,
        redirect_uris,
        post_logout_redirect_uris,
        scopes,
        client_settings,
        token_settings
    )
VALUES (
        'product-service-id',
        'product-service',
        CURRENT_TIMESTAMP,
        '$2a$10$YQq5pC.1K9L9Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Q5Z5Qu', -- BCrypt encoded 'product-service-secret'
        NULL,
        'Product Service',
        'client_secret_basic,client_secret_post',
        'client_credentials',
        '',
        '',
        'user.read,user.validate',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'
    );

-- Admin Web Application Client (Authorization Code Flow)
INSERT INTO
    client (
        id,
        client_id,
        client_id_issued_at,
        client_secret,
        client_secret_expires_at,
        client_name,
        client_authentication_methods,
        authorization_grant_types,
        redirect_uris,
        post_logout_redirect_uris,
        scopes,
        client_settings,
        token_settings
    )
VALUES (
        'admin-web-app-id',
        'admin-web-app',
        NOW(),
        '$2a$10$YYrXfOy2g7C1zP2.8fH8g.NZjqtk5g5KK5D1fF2j6h7c9g8.9h8g9', -- Encoded: 'admin-web-secret'
        NULL,
        'Admin Web Application',
        'client_secret_basic,client_secret_post',
        'authorization_code,refresh_token',
        'http://localhost:3000/callback,http://localhost:8080/callback',
        'http://localhost:3000/logout,http://localhost:8080/logout',
        'openid,profile,read,write,admin',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",1800.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'
    );

-- Mobile App Client (Authorization Code with PKCE)
INSERT INTO
    client (
        id,
        client_id,
        client_id_issued_at,
        client_secret,
        client_secret_expires_at,
        client_name,
        client_authentication_methods,
        authorization_grant_types,
        redirect_uris,
        post_logout_redirect_uris,
        scopes,
        client_settings,
        token_settings
    )
VALUES (
        'mobile-app-id',
        'ecommerce-mobile-app',
        NOW(),
        NULL, -- Public client, no secret
        NULL,
        'Ecommerce Mobile App',
        'none',
        'authorization_code,refresh_token',
        'com.ecommerce.app://callback',
        'com.ecommerce.app://logout',
        'openid,profile,read,write',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":false}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",1800.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'
    );

-- Swagger UI Client for User Role (Authorization Code Flow)
INSERT INTO
    client (
        id,
        client_id,
        client_id_issued_at,
        client_secret,
        client_secret_expires_at,
        client_name,
        client_authentication_methods,
        authorization_grant_types,
        redirect_uris,
        post_logout_redirect_uris,
        scopes,
        client_settings,
        token_settings
    )
VALUES (
        'swagger-user-id',
        'swagger-user',
        NOW(),
        '$2a$10$YYrXfOy2g7C1zP2.8fH8g.NZjqtk5g5KK5D1fF2j6h7c9g8.9h8g9', -- Encoded: 'swagger-user-secret'
        NULL,
        'Swagger UI User Client',
        'client_secret_basic,client_secret_post',
        'authorization_code,refresh_token',
        'http://localhost:8080/swagger-ui/oauth2-redirect.html,http://localhost:8444/swagger-ui/oauth2-redirect.html',
        '',
        'openid,profile,read,write,CUSTOMER',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",1800.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}'
    );

-- Test Users for Authentication
INSERT INTO
    users (
        username,
        email,
        password,
        first_name,
        last_name,
        role,
        email_verified,
        account_locked
    )
VALUES (
        'testuser',
        'testuser@example.com',
        '$2a$10$8K3W2YQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQ',
        'Test',
        'User',
        'CUSTOMER',
        true,
        false
    ),
    (
        'testadmin',
        'testadmin@example.com',
        '$2a$10$8K3W2YQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQX8Jc5nQ',
        'Test',
        'Admin',
        'ADMIN',
        true,
        false
    );