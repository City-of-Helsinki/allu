-- ALLU-207: Add composite covering indexes to speed up findPurgeableCustomerIds.
-- Using CREATE INDEX CONCURRENTLY to avoid table-level locks on high-write tables.
-- Note: single-column index change_history_customer_id_index already exists from V185;
--       we add only the composite variant for that table.

-- change_history: composite for MAX(change_time) per customer_id
CREATE INDEX CONCURRENTLY change_history_customer_id_change_time_index
  ON allu.change_history (customer_id, change_time);

-- customer_update_log: missing index; needed for MAX(update_time) per customer_id
CREATE INDEX CONCURRENTLY customer_update_log_customer_id_update_time_index
  ON allu.customer_update_log (customer_id, update_time);

-- person_audit_log: missing index for customer-level lookup
CREATE INDEX CONCURRENTLY person_audit_log_customer_id_creation_time_index
  ON allu.person_audit_log (customer_id, creation_time);

-- person_audit_log: missing index for contact-level lookup (nested subquery)
CREATE INDEX CONCURRENTLY person_audit_log_contact_id_creation_time_index
  ON allu.person_audit_log (contact_id, creation_time);

-- contact: needed for the IN subquery feeding person_audit_log (customer_id → contact IDs)
CREATE INDEX CONCURRENTLY contact_customer_id_index
  ON allu.contact (customer_id);

