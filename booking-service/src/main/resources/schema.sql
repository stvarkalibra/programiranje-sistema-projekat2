CREATE TABLE IF NOT EXISTS guest (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS cancellation_policy (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_name       VARCHAR(255),
    refund_percentage INTEGER
);

CREATE TABLE IF NOT EXISTS booking (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    unit_id         BIGINT,
    guest_id        BIGINT,
    cancellation_policy_id BIGINT,
    start_date      DATE,
    end_date        DATE,
    total_price     DOUBLE,
    status          VARCHAR(255),
    FOREIGN KEY (guest_id)               REFERENCES guest(id),
    FOREIGN KEY (cancellation_policy_id) REFERENCES cancellation_policy(id)
);

CREATE TABLE IF NOT EXISTS add_on_item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id   BIGINT,
    service_name VARCHAR(255),
    price        DOUBLE,
    FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE TABLE IF NOT EXISTS booking_status_log (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id   BIGINT,
    old_status   VARCHAR(255),
    new_status   VARCHAR(255),
    changed_date TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES booking(id)
);
