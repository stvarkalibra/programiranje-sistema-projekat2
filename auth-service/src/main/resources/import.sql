INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('GUEST');
INSERT INTO roles (name) VALUES ('GUIDE');

INSERT INTO users (username, api_key) VALUES ('admin_user', 'admin-key-123');
INSERT INTO users (username, api_key) VALUES ('guest_user', 'guest-key-456');
INSERT INTO users (username, api_key) VALUES ('guide_user', 'guide-key-789');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);