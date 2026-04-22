-- Poistetaan vanhat, virheelliset tai tarpeettomat taulut
drop table if exists allu.deleted_customer_archive;
drop table if exists allu.deletable_customer;

-- Luodaan uusi arkistotaulu
create table allu.customer_archive (
  id serial primary key,
  customer_id integer not null,
  sap_customer_number text,
  deleted_at timestamp with time zone not null
);
