ALTER TABLE parent ADD COLUMN IF NOT EXISTS assigned_health_worker_id uuid REFERENCES health_worker(id);
CREATE INDEX IF NOT EXISTS idx_parent_assigned_health_worker ON parent (assigned_health_worker_id);
