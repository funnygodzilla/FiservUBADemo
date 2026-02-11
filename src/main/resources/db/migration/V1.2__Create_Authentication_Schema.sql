-- V1.2__Create_Authentication_Schema.sql
-- Authentication and Authorization Schema
-- Date: 2026-02-10

-- =====================================================
-- Table: users
-- Description: User accounts with authentication info
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    customer_id BIGINT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    account_expired BOOLEAN NOT NULL DEFAULT FALSE,
    credentials_expired BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_customer_id (customer_id),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: roles
-- Description: User roles for RBAC
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: permissions
-- Description: Fine-grained permissions
-- =====================================================
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200),
    resource VARCHAR(50),
    action VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_resource (resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: user_roles
-- Description: Many-to-many relationship between users and roles
-- =====================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: role_permissions
-- Description: Many-to-many relationship between roles and permissions
-- =====================================================
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Insert Default Roles
-- =====================================================
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access'),
('MANAGER', 'Manager with elevated permissions'),
('USER', 'Regular user with basic permissions'),
('CUSTOMER', 'Customer with account access')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =====================================================
-- Insert Default Permissions
-- =====================================================
INSERT INTO permissions (name, description, resource, action) VALUES
-- Account permissions
('account:create', 'Create new accounts', 'account', 'create'),
('account:read', 'Read account information', 'account', 'read'),
('account:update', 'Update account information', 'account', 'update'),
('account:delete', 'Delete accounts', 'account', 'delete'),
('account:freeze', 'Freeze/unfreeze accounts', 'account', 'freeze'),

-- Transaction permissions
('transaction:create', 'Create transactions', 'transaction', 'create'),
('transaction:read', 'Read transaction history', 'transaction', 'read'),
('transaction:write', 'Perform debit/credit operations', 'transaction', 'write'),
('transaction:reverse', 'Reverse transactions', 'transaction', 'reverse'),

-- User permissions
('user:create', 'Create new users', 'user', 'create'),
('user:read', 'Read user information', 'user', 'read'),
('user:update', 'Update user information', 'user', 'update'),
('user:delete', 'Delete users', 'user', 'delete'),

-- Report permissions
('report:read', 'View reports', 'report', 'read'),
('report:generate', 'Generate reports', 'report', 'generate')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =====================================================
-- Assign Permissions to Roles
-- =====================================================

-- ADMIN: All permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- MANAGER: Most permissions except user management
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'MANAGER'
AND p.name IN (
    'account:create', 'account:read', 'account:update', 'account:freeze',
    'transaction:create', 'transaction:read', 'transaction:write',
    'report:read', 'report:generate'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- USER: Basic permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'USER'
AND p.name IN (
    'account:read',
    'transaction:create', 'transaction:read'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- CUSTOMER: Read-only access
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'CUSTOMER'
AND p.name IN (
    'account:read',
    'transaction:read'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- =====================================================
-- Create Default Admin User
-- Password: Admin@123 (BCrypt encoded with strength 10)
-- =====================================================
INSERT INTO users (username, email, password, first_name, last_name, enabled, customer_id)
VALUES (
    'admin',
    'admin@fiserv.com',
    '$2a$10$mQpM99Vvwx5sdQ0TBeSdguLVjQrJ2YON/DUqK.M4XOANTJULe5ErO',
    'System',
    'Administrator',
    TRUE,
    NULL
)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- =====================================================
-- Create Test Users
-- =====================================================

-- Manager User (password: Manager@123 - BCrypt encoded with strength 10)
INSERT INTO users (username, email, password, first_name, last_name, enabled, customer_id)
VALUES (
    'manager1',
    'manager1@fiserv.com',
    '$2a$10$3gRVrHj/s0ZwSNNTufTZ5.T5uk3Ug0PcTzcIv7DlOHfLh0sGLGSWO',
    'John',
    'Manager',
    TRUE,
    1001
)
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'manager1' AND r.name = 'MANAGER'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Regular User (password: User@123 - BCrypt encoded with strength 10)
INSERT INTO users (username, email, password, first_name, last_name, enabled, customer_id)
VALUES (
    'user1',
    'user1@fiserv.com',
    '$2a$10$.GSG3vuZavBXPS5JiodkVeejkPE24VbDx3c7cb/pD/kanMYhJZLhu',
    'Alice',
    'User',
    TRUE,
    1002
)
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'user1' AND r.name = 'USER'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Customer User (password: Customer@123 - BCrypt encoded with strength 10)
INSERT INTO users (username, email, password, first_name, last_name, enabled, customer_id)
VALUES (
    'customer1',
    'customer1@fiserv.com',
    '$2a$10$nVM5aF3UWsus6lgDJh.9Gu4AukT0jlA/7TURljJHRlhoaqcDDNIEO',
    'Bob',
    'Customer',
    TRUE,
    1003
)
ON DUPLICATE KEY UPDATE email = VALUES(email);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'customer1' AND r.name = 'CUSTOMER'
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

