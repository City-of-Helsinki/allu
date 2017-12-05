create table allu.deposit (
  id serial primary key,
  application_id integer unique not null references allu.application(id) on delete cascade,
  amount integer not null,
  reason text,
  paid boolean not null default false,
  creator_id integer references allu.user(id),
  creation_time timestamp with time zone not null
);
