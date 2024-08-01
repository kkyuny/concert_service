-- Insert user table
INSERT INTO "user" (amount, regi_date) VALUES (1000, CURRENT_TIMESTAMP);
INSERT INTO "user" (amount, regi_date) VALUES (2000, CURRENT_TIMESTAMP);
INSERT INTO "user" (amount, regi_date) VALUES (3000, CURRENT_TIMESTAMP);
INSERT INTO "user" (amount, regi_date) VALUES (4000, CURRENT_TIMESTAMP);
INSERT INTO "user" (amount, regi_date) VALUES (5000, CURRENT_TIMESTAMP);

-- Insert queue table
INSERT INTO queue (token, status, user_id, regi_date) VALUES ('testToken1', 'waiting', 1, now());
INSERT INTO queue (token, status, user_id, regi_date) VALUES ('testToken2', 'active', 2, now());
INSERT INTO queue (token, status, user_id, regi_date) VALUES ('testToken3', 'waiting', 3, now());

-- Insert concert table
INSERT INTO concert (title, regi_date) VALUES ('Test Concert', CURRENT_TIMESTAMP);

-- Insert concert_schedule table
INSERT INTO concert_schedule (concert_id, price, concert_date, regi_date) VALUES (1, 100, '2024-08-05 00:00:00', CURRENT_TIMESTAMP);
INSERT INTO concert_schedule (concert_id, price, concert_date, regi_date) VALUES (1, 150, '2024-08-06 00:00:00', CURRENT_TIMESTAMP);
INSERT INTO concert_schedule (concert_id, price, concert_date, regi_date) VALUES (1, 200, '2024-08-07 00:00:00', CURRENT_TIMESTAMP);

-- Insert initial data concert_reservation table
INSERT INTO concert_reservation (concert_id, concert_date, seat_no, user_id, status, regi_date)
VALUES (1, '2024-08-05 00:00:00', 1, 1, 'reserved', CURRENT_TIMESTAMP);
