CREATE TABLE transactions(
    id UUID PRIMARY KEY,
    amount NUMERIC(18, 8) NOT NULL,
    fee NUMERIC(18, 8) NOT NULL,
    fiat_rate NUMERIC(18, 8) NOT NULL ,
    price NUMERIC(18, 8) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP(6) with time zone NOT NULL,
    type VARCHAR(255) NOT NULL
)