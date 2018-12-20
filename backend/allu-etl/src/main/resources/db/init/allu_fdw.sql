-- Create user in Allu operative database.
CREATE USER allu_reporting PASSWORD '<a-fancy-password-here>';
GRANT CONNECT ON DATABASE jenkins TO allu_reporting;
GRANT USAGE ON SCHEMA allu to allu_reporting;
GRANT SELECT ON ALL TABLES IN SCHEMA allu TO allu_reporting;
ALTER DEFAULT PRIVILEGES IN SCHEMA allu GRANT SELECT ON TABLES TO allu_reporting;

--
-- Create the foreign data wrapper in reporting database.
CREATE SERVER allu_operative
FOREIGN DATA WRAPPER postgres_fdw
OPTIONS (host 'localhost', dbname 'jenkins', port '5432', updatable 'false');
ALTER SERVER allu_operative OWNER TO jenkins_report;


-- Create mapping from reporting database user to Allu operative database user.
CREATE USER MAPPING FOR jenkins_report
SERVER allu_operative
OPTIONS (user 'allu_reporting', password 'allu_reporting');

-- Import the foreign tables from Allu operative database.
CREATE SCHEMA IF NOT EXISTS allu_operative;

IMPORT FOREIGN SCHEMA allu
LIMIT TO (
  application,
  application_attachment,
  application_customer,
  application_kind,
  application_tag,
  attachment,
  attachment_data,
  change_history,
  charge_basis,
  city_district,
  codeset,
  comment,
  contact,
  contract,
  customer,
  decision,
  deposit,
  distribution_entry,
  field_change,
  fixed_location,
  information_request,
  information_request_field,
  invoice,
  invoice_recipient,
  invoice_row,
  kind_specifier,
  location,
  location_area,
  location_flids,
  location_geometry,
  postal_address,
  project,
  recurring_period,
  supervision_task,
  "user",
  invoicing_period,
  customer_location_validity
)
FROM SERVER allu_operative INTO allu_operative;