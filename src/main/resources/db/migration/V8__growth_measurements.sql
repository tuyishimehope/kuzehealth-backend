CREATE TABLE IF NOT EXISTS growth_measurement (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    deleted_at timestamp(6) without time zone,
    infant_id uuid NOT NULL REFERENCES infant(id),
    measured_at date NOT NULL,
    weight_kg double precision,
    height_cm double precision,
    head_circumference_cm double precision,
    muac_cm double precision,
    notes character varying(500),
    recorded_by character varying(255)
);
CREATE INDEX IF NOT EXISTS idx_growth_measurement_infant ON growth_measurement (infant_id, measured_at);
