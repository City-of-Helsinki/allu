-- ALLU-207 (follow-up): isNotLinkedToAnyEntity() in CustomerDao also checks
-- project.customer_id and application.invoice_recipient_id via NOT EXISTS
-- subqueries, but neither column had a supporting index. V200 only covered
-- the change_history/customer_update_log/person_audit_log/contact lookups
-- used by findPurgeableCustomerIds' MAX(...) subqueries, leaving these two
-- checks doing full table scans per candidate customer on large project/
-- application tables. Adding the missing indexes here.
--
-- Using CREATE INDEX CONCURRENTLY to avoid table-level locks on write-heavy
-- tables. IF NOT EXISTS is used so a retry after a partially-applied/failed
-- run does not error out on indexes that were already created.

-- project: needed for NOT EXISTS (... FROM project WHERE customer_id = X)
CREATE INDEX CONCURRENTLY IF NOT EXISTS project_customer_id_index
  ON allu.project (customer_id);

-- application: needed for NOT EXISTS (... FROM application WHERE invoice_recipient_id = X)
CREATE INDEX CONCURRENTLY IF NOT EXISTS application_invoice_recipient_id_index
  ON allu.application (invoice_recipient_id);
