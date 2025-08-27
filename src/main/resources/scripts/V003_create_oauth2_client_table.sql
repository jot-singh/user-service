-- V003: Create OAuth2 Client Registration Table
-- This script creates the necessary table for storing OAuth2 client registrations

-- Create client table for OAuth2 Authorization Server
CREATE TABLE IF NOT EXISTS `client` (
    `id` VARCHAR(100) NOT NULL,
    `client_id` VARCHAR(100) NOT NULL,
    `client_id_issued_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `client_secret` VARCHAR(200) DEFAULT NULL,
    `client_secret_expires_at` TIMESTAMP NULL,
    `client_name` VARCHAR(200) NOT NULL,
    `client_authentication_methods` TEXT NOT NULL,
    `authorization_grant_types` TEXT NOT NULL,
    `redirect_uris` TEXT DEFAULT NULL,
    `post_logout_redirect_uris` TEXT DEFAULT NULL,
    `scopes` TEXT NOT NULL,
    `client_settings` TEXT NOT NULL,
    `token_settings` TEXT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create index for faster client lookups
CREATE INDEX IF NOT EXISTS `idx_client_client_id` ON `client` (`client_id`);

-- Add comments for documentation
ALTER TABLE `client` COMMENT = 'OAuth2 client registrations for authorization server';
