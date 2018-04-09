create table allu.person_audit_log (
  id serial primary key,
  customer_id integer references allu.customer(id),
  contact_id integer references allu.contact(id),
  user_id integer not null references allu.user(id),
  source text not null,
  creation_time timestamp with time zone not null,
  constraint allu_customer_or_contact_ck check ((customer_id is null) != (contact_id is null)));
