INSERT OR IGNORE INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT OR IGNORE INTO roles (id, name) VALUES (2, 'GUEST');
INSERT OR IGNORE INTO roles (id, name) VALUES (3, 'GUIDE');

INSERT OR IGNORE INTO users (id, username, api_key) VALUES (1, 'admin_user', 'admin-key-123');
INSERT OR IGNORE INTO users (id, username, api_key) VALUES (2, 'guest_user', 'guest-key-456');
INSERT OR IGNORE INTO users (id, username, api_key) VALUES (3, 'guide_user', 'guide-key-789');

INSERT OR IGNORE INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT OR IGNORE INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT OR IGNORE INTO user_roles (user_id, role_id) VALUES (3, 3);