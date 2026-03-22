ALTER TABLE account_transfer_lock
DROP COLUMN locked_at;

ALTER TABLE account_transfer_lock
DROP COLUMN is_locked;

ALTER TABLE account_transfer_lock
ADD COLUMN last_transfer_date TIMESTAMP NULL DEFAULT NULL;