create table allu.deletable_customer (
  customer_id integer not null primary key references allu.customer(id) on delete cascade,
  sap_customer_number text
);

create table allu.customer_archive (
  id serial primary key,
  customer_id integer not null,
  sap_customer_number text
);
