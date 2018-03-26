create table allu.stored_filter (
  id serial primary key,
  type text not null,
  name text not null,
  default_filter boolean default false,
  filter text,
  user_id integer references allu.user(id));