-- V1__init_schema.sql

-- Tabla principal de usuarios
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Tabla roles
CREATE TABLE account_roles (
    account_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);