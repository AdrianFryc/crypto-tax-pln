CREATE TABLE users(
                             id UUID PRIMARY KEY,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             password_hash VARCHAR(255) NOT NULL,
                             first_name VARCHAR(255) NOT NULL,
                             last_name VARCHAR(255) NOT NULL,
                             created_at TIMESTAMP WITH TIME ZONE NOT NULL

                  );

INSERT INTO users (id, email, password_hash, first_name, last_name, created_at) VALUES ('00000000-0000-0000-0000-000000000001', 'default@user.com', 'test', 'Default_NAME', 'Default_LAST_NAME', '2023-01-01 00:00:00');


ALTER TABLE transactions ADD COLUMN user_id UUID;

UPDATE transactions SET user_id = '00000000-0000-0000-0000-000000000001' where user_id is null;
ALTER TABLE transactions ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE transactions ADD CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id);