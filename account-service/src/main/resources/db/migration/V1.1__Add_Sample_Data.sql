-- V1.1__Add_Sample_Data.sql
-- Sample data for development and testing
-- Date: 2026-02-06

-- Insert sample accounts
INSERT INTO accounts (account_number, customer_id, account_type, status, balance, currency, created_at, updated_at) VALUES
('ACC1707210600ABCD1234', 1001, 'CHECKING', 'ACTIVE', 5000.00, 'USD', NOW(), NOW()),
('ACC1707210601EFGH5678', 1002, 'SAVINGS', 'ACTIVE', 10000.00, 'USD', NOW(), NOW()),
('ACC1707210602IJKL9012', 1003, 'CHECKING', 'ACTIVE', 2500.00, 'USD', NOW(), NOW()),
('ACC1707210603MNOP3456', 1001, 'BUSINESS', 'ACTIVE', 50000.00, 'USD', NOW(), NOW());
