CREATE TABLE IF NOT EXISTS vaccine_schedule_item (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    code character varying(32) NOT NULL UNIQUE,
    vaccine_name character varying(255) NOT NULL,
    dose_number integer NOT NULL,
    due_age_days integer NOT NULL,
    description character varying(500),
    active boolean NOT NULL DEFAULT true
);

ALTER TABLE vaccination ADD COLUMN IF NOT EXISTS schedule_code character varying(32);

-- Routine infant immunisation schedule (Rwanda EPI). This is reference data: confirm it
-- against the current Ministry of Health / RBC schedule and adjust it through the
-- /api/v1/immunisation/schedule endpoints rather than by editing this file.
INSERT INTO vaccine_schedule_item (id, created_at, updated_at, code, vaccine_name, dose_number, due_age_days, description) VALUES
    (gen_random_uuid(), now(), now(), 'BCG',    'BCG',                          1,   0, 'Tuberculosis, at birth'),
    (gen_random_uuid(), now(), now(), 'OPV0',   'Oral polio vaccine',           0,   0, 'Polio, birth dose'),
    (gen_random_uuid(), now(), now(), 'OPV1',   'Oral polio vaccine',           1,  42, 'Polio, 6 weeks'),
    (gen_random_uuid(), now(), now(), 'PENTA1', 'Pentavalent (DTP-HepB-Hib)',   1,  42, 'Diphtheria, tetanus, pertussis, hepatitis B, Hib, 6 weeks'),
    (gen_random_uuid(), now(), now(), 'PCV1',   'Pneumococcal conjugate',       1,  42, 'Pneumococcal disease, 6 weeks'),
    (gen_random_uuid(), now(), now(), 'ROTA1',  'Rotavirus',                    1,  42, 'Rotavirus diarrhoea, 6 weeks'),
    (gen_random_uuid(), now(), now(), 'OPV2',   'Oral polio vaccine',           2,  70, 'Polio, 10 weeks'),
    (gen_random_uuid(), now(), now(), 'PENTA2', 'Pentavalent (DTP-HepB-Hib)',   2,  70, '10 weeks'),
    (gen_random_uuid(), now(), now(), 'PCV2',   'Pneumococcal conjugate',       2,  70, '10 weeks'),
    (gen_random_uuid(), now(), now(), 'ROTA2',  'Rotavirus',                    2,  70, '10 weeks'),
    (gen_random_uuid(), now(), now(), 'OPV3',   'Oral polio vaccine',           3,  98, 'Polio, 14 weeks'),
    (gen_random_uuid(), now(), now(), 'PENTA3', 'Pentavalent (DTP-HepB-Hib)',   3,  98, '14 weeks'),
    (gen_random_uuid(), now(), now(), 'PCV3',   'Pneumococcal conjugate',       3,  98, '14 weeks'),
    (gen_random_uuid(), now(), now(), 'IPV1',   'Inactivated polio vaccine',    1,  98, '14 weeks'),
    (gen_random_uuid(), now(), now(), 'MR1',    'Measles-rubella',              1, 274, '9 months'),
    (gen_random_uuid(), now(), now(), 'MR2',    'Measles-rubella',              2, 456, '15 months')
ON CONFLICT (code) DO NOTHING;
