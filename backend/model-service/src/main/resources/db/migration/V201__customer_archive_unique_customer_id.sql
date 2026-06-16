-- ALLU-207: Add UNIQUE constraint on customer_archive.customer_id.
-- Required before archiveCustomers can use ON CONFLICT (customer_id) DO NOTHING.
-- Step 1: Remove duplicate rows, keeping the row with the smallest id per customer_id.
-- Step 2: Add the UNIQUE constraint.

DELETE FROM allu.customer_archive
WHERE id NOT IN (
  SELECT MIN(id)
  FROM allu.customer_archive
  GROUP BY customer_id
);

ALTER TABLE allu.customer_archive
  ADD CONSTRAINT customer_archive_customer_id_unique UNIQUE (customer_id);
