-- V2__add_account_status_and_created_at.sql

-- Agregar columna status a la tabla accounts
ALTER TABLE accounts ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE';

-- Agregar columna created_at a la tabla accounts
ALTER TABLE accounts ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Crear índice para la columna status (útil para consultas por estado)
CREATE INDEX idx_accounts_status ON accounts(status);

-- Crear índice para la columna created_at (útil para consultas por fecha de creación)
CREATE INDEX idx_accounts_created_at ON accounts(created_at);