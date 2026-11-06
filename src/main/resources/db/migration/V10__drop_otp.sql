-- One-time login codes were removed; sign-in is now email or phone number plus password.
ALTER TABLE users DROP COLUMN IF EXISTS otp;
ALTER TABLE users DROP COLUMN IF EXISTS otp_expiration_time;
ALTER TABLE users DROP COLUMN IF EXISTS otp_attempts;
