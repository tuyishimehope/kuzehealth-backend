ALTER TABLE users ADD COLUMN IF NOT EXISTS tokens_invalid_before bigint;

CREATE TABLE IF NOT EXISTS refresh_token (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    token_hash character varying(64) NOT NULL UNIQUE,
    expires_at timestamp(6) without time zone NOT NULL,
    revoked boolean NOT NULL DEFAULT false,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_token (user_id);
