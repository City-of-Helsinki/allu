-- Index for attachment cleanup in deleteAttachmentData() — speeds up the refcount check
-- (SELECT count(*) FROM attachment WHERE attachment_data_id = ?) which previously required
-- a full sequential scan of the attachment table.
CREATE INDEX attachment_attachment_data_id_index ON allu.attachment (attachment_data_id);

-- Composite index for the correlated MAX(change_time) subquery in findAnonymizableApplications().
-- The query does: SELECT MAX(change_time) FROM change_history WHERE application_id = ?
-- for every row in anonymizable_application. This index allows that to be satisfied via
-- an index-only scan instead of scanning all change_history rows per application.
CREATE INDEX change_history_application_id_change_time_index ON allu.change_history (application_id, change_time DESC);
