ALTER TABLE transactions
    RENAME COLUMN symbol TO crypto_symbol;
ALTER TABLE transactions
    ADD COLUMN fiat_currency VARCHAR(255) NOT NULL;
