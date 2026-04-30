-- V1__init_auth_schema.sql

-- 1. Tabla principal de cuentas
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Tabla para la colección de roles (@ElementCollection)
CREATE TABLE account_roles (
    account_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    
    -- Constraint guapa: Si borras la cuenta, te cargas sus roles automáticamente (Clean data)
    CONSTRAINT fk_account_roles_account_id 
        FOREIGN KEY (account_id) 
        REFERENCES accounts (id) 
        ON DELETE CASCADE
);

-- 3. Índice estratégico
-- Como el @ElementCollection hace join por el account_id contínuamente, 
-- si no le metes un índice aquí, la BBDD va a sufrir cuando tengas 100k usuarios.
CREATE INDEX idx_account_roles_account_id ON account_roles(account_id);