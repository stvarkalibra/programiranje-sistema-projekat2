CREATE TABLE IF NOT EXISTS roles (
     id BIGINT PRIMARY KEY AUTOINCREMENT,
     name VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id       BIGINT PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    api_key  VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id)
    );