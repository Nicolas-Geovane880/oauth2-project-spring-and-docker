ALTER TABLE client_account
MODIFY COLUMN balance DECIMAL (19, 2) NOT NULL;

ALTER TABLE account_transfer_lock
MODIFY COLUMN total_value_transferred_today DECIMAL (19, 2) NOT NULL DEFAULT 0.0;