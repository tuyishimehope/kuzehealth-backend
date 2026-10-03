-- Existing parents were already being messaged, so consent defaults to true for them.
ALTER TABLE parent ADD COLUMN IF NOT EXISTS sms_consent boolean NOT NULL DEFAULT true;
ALTER TABLE parent ADD COLUMN IF NOT EXISTS preferred_language character varying(8) NOT NULL DEFAULT 'EN';

CREATE TABLE IF NOT EXISTS consent_record (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    parent_id uuid NOT NULL REFERENCES parent(id),
    consent_type character varying(32) NOT NULL,
    granted boolean NOT NULL,
    recorded_by character varying(255),
    note character varying(500)
);
CREATE INDEX IF NOT EXISTS idx_consent_record_parent ON consent_record (parent_id);

CREATE TABLE IF NOT EXISTS sms_log (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    recipient character varying(64),
    message text,
    sender character varying(32),
    purpose character varying(32) NOT NULL,
    status character varying(32) NOT NULL,
    provider_response text,
    parent_id uuid,
    triggered_by character varying(255)
);
CREATE INDEX IF NOT EXISTS idx_sms_log_created_at ON sms_log (created_at);
CREATE INDEX IF NOT EXISTS idx_sms_log_parent ON sms_log (parent_id);
