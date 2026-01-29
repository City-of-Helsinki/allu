create table allu.customer_archive (
  id serial primary key,
  customer_id integer not null,
  sap_customer_number text not null
);
