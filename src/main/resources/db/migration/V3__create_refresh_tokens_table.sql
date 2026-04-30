-- V3__create_refresh_tokens_table.sql

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    account_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    replaced_by_token VARCHAR(255),
    
    CONSTRAINT fk_refresh_tokens_account 
        FOREIGN KEY (account_id) 
        REFERENCES accounts (id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_account_id ON refresh_tokens(account_id);