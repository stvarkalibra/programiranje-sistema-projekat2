CREATE TABLE IF NOT EXISTS tax_rate (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    tax_name   VARCHAR(255),
    percentage DOUBLE
);

CREATE TABLE IF NOT EXISTS invoice (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    tax_rate_id  BIGINT,
    booking_id   BIGINT,
    arrival_date DATE,
    subtotal     DOUBLE,
    total_amount DOUBLE,
    status       VARCHAR(255),
    FOREIGN KEY (tax_rate_id) REFERENCES tax_rate(id)
);

CREATE TABLE IF NOT EXISTS payment_method (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    method_name VARCHAR(255),
    is_active   BOOLEAN
);

CREATE TABLE IF NOT EXISTS "transaction" (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id         BIGINT,
    payment_method_id  BIGINT,
    amount             DOUBLE,
    transaction_date   TIMESTAMP,
    FOREIGN KEY (invoice_id)        REFERENCES invoice(id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_method(id)
);
