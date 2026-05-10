CREATE TABLE IF NOT EXISTS roles (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username VARCHAR(100) NOT NULL UNIQUE,
    api_key VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id INTEGER NOT NULL,
                                          role_id INTEGER NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
    );