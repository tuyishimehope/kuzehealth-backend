-- Clinical records are never physically deleted; deleted_at marks them as removed.
ALTER TABLE parent ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
ALTER TABLE infant ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
ALTER TABLE visit ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
ALTER TABLE visit_note ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
ALTER TABLE vaccination ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
ALTER TABLE pregnancy_record ADD COLUMN IF NOT EXISTS deleted_at timestamp(6) without time zone;
