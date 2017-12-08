create table allu.deposit (
  id serial primary key,
  application_id integer unique not null references allu.application(id) on delete cascade,
  amount integer not null,
  reason text,
  status text not null default 'UNPAID_DEPOSIT',
  creator_id integer references allu.user(id),
  creation_time timestamp with time zone not null
);
