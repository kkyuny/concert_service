-- Drop existing table
DROP TABLE IF EXISTS queue;

CREATE TABLE "user" (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount BIGINT DEFAULT 0,
    regi_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE queue (
    token VARCHAR(255) PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    valid_date TIMESTAMP,
    regi_date TIMESTAMP
);

CREATE TABLE concert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    regi_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE concert_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_id BIGINT NOT NULL,
    price BIGINT NOT NULL,
    concert_date TIMESTAMP NOT NULL,
    regi_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (concert_id) REFERENCES concert(id)
);

CREATE TABLE concert_reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_id BIGINT NOT NULL,
    concert_date TIMESTAMP NOT NULL,
    seat_no BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    valid_date TIMESTAMP,
    regi_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT,
    FOREIGN KEY (concert_id) REFERENCES concert(id)
);

CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    concert_id BIGINT NOT NULL,
    concert_date TIMESTAMP NOT NULL,
    amount BIGINT NOT NULL,
    seat_no BIGINT,
    regi_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT,
    payment_id BIGINT NOT NULL,
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE payment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    concert_id BIGINT NOT NULL,
    concert_date TIMESTAMP NOT NULL,
    amount BIGINT NOT NULL,
    seat_no BIGINT,
    regi_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);