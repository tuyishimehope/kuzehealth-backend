ALTER TABLE visit ADD COLUMN IF NOT EXISTS reminder_sent boolean NOT NULL DEFAULT false;
CREATE INDEX IF NOT EXISTS idx_visit_scheduled_time ON visit (scheduled_time);
CREATE INDEX IF NOT EXISTS idx_vaccination_next_due_date ON vaccination (next_due_date);
