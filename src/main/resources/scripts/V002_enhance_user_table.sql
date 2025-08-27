-- Migration script for enhanced User table
-- Adds ecommerce-specific fields and constraints

-- Add new columns to users table
ALTER TABLE users 
ADD COLUMN first_name VARCHAR(50),
ADD COLUMN last_name VARCHAR(50),
ADD COLUMN phone VARCHAR(20),
ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN account_locked BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0,
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN last_login TIMESTAMP NULL;

-- Update role column to support new roles
ALTER TABLE users MODIFY COLUMN role ENUM('CUSTOMER', 'MERCHANT', 'ADMIN', 'MODERATOR', 'USER') NOT NULL DEFAULT 'CUSTOMER';

-- Add constraints
ALTER TABLE users 
ADD CONSTRAINT uk_users_email UNIQUE (email),
ADD CONSTRAINT uk_users_username UNIQUE (username);

-- Add indexes for better performance
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_role ON users(role);

-- Update existing users to have email_verified = true (for backward compatibility)
UPDATE users SET email_verified = TRUE WHERE email_verified IS NULL;

-- Update existing USER roles to CUSTOMER
UPDATE users SET role = 'CUSTOMER' WHERE role = 'USER';
