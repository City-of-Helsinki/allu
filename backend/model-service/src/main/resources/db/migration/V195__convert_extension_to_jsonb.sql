-- Convert application.extension column from TEXT to JSONB
-- This enables native JSONB indexing and operations

ALTER TABLE allu.application
ALTER COLUMN extension TYPE jsonb USING extension::jsonb;
