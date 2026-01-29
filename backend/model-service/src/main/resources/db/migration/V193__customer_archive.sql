create table allu.customer_archive (
  id serial primary key,
  customer_id integer not null,
  sap_customer_number text,
  deleted_at timestamp with time zone not null,
  notification_sent_at timestamp with time zone
);
