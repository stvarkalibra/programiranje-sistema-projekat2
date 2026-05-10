INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'GUEST');
INSERT INTO roles (id, name) VALUES (3, 'GUIDE');

INSERT INTO users (id, username, api_key) VALUES (1, 'admin_user', 'admin-key-123');
INSERT INTO users (id, username, api_key) VALUES (2, 'guest_user', 'guest-key-456');
INSERT INTO users (id, username, api_key) VALUES (3, 'guide_user', 'guide-key-789');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);