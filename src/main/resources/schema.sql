CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL -- ADMIN или USER
);

CREATE TABLE IF NOT EXISTS otp_config (
    id INT PRIMARY KEY CHECK (id = 1),
    code_length INT NOT NULL DEFAULT 6,
    ttl_seconds INT NOT NULL DEFAULT 300
);

-- Вставляем дефолтную конфигурацию (если ее нет)
INSERT INTO otp_config (id, code_length, ttl_seconds)
VALUES (1, 6, 300) ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS otp_codes (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    operation_id VARCHAR(100),
    code VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVE, EXPIRED, USED
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);