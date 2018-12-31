create table allu.customer_update_log (
  id serial primary key,
  customer_id integer references allu.customer(id),
  update_time timestamp with time zone,
  processed_time timestamp with time zone
);