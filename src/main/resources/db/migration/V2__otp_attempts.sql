ALTER TABLE users ADD COLUMN IF NOT EXISTS otp_attempts integer NOT NULL DEFAULT 0;

-- Pending login codes were stored in clear text; they are now stored hashed.
UPDATE users SET otp = NULL, otp_expiration_time = NULL;
