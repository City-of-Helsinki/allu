create table allu.invoicing_period (
  id serial primary key,
  application_id integer not null references allu.application(id) on delete cascade,
  invoiced boolean not null,
  start_time timestamp with time zone not null,
  end_time timestamp with time zone not null
);

alter table allu.invoice add column invoicing_period_id integer references allu.invoicing_period(id) on delete cascade;
alter table allu.charge_basis add column invoicing_period_id integer references allu.invoicing_period(id) on delete cascade;
alter table allu.charge_basis add column location_id integer references allu.location(id) on delete cascade;

