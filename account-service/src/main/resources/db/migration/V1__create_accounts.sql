CREATE TABLE accounts (
  account_id VARCHAR(64) PRIMARY KEY,
  status VARCHAR(32) NOT NULL,
  currency VARCHAR(8) NOT NULL,
  balance DECIMAL(19, 4) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NULL
);
