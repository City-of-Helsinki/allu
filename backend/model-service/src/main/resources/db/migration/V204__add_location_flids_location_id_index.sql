-- location_flids.location_id had no supporting index, so every per-location
-- lookup (e.g. resolving fixed location area names for application addresses)
-- did a full table scan. Needed by the supervision_task_with_address view,
-- which resolves addresses per application.
--
-- The table is small, so a plain CREATE INDEX is used. It runs inside the
-- migration transaction, so a failed run rolls back cleanly instead of
-- leaving an invalid index behind as CREATE INDEX CONCURRENTLY could.
CREATE INDEX location_flids_location_id_index
  ON allu.location_flids (location_id);
