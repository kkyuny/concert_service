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
INSERT INTO concert (title, regi_date) VALUES ('Concert', CURRENT_TIMESTAMP);


