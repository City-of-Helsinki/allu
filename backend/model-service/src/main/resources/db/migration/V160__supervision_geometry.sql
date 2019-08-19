create table allu.supervision_task_location (
  id serial primary key,
  application_location_id integer,
  location_key integer,
  payment_tariff text,
  start_time timestamp with time zone not null,
  end_time timestamp with time zone not null,
  underpass boolean
);

-- link table between task and approved location
create table supervision_task_approved_location (
  supervision_task_id integer references allu.supervision_task(id) on delete cascade,
  supervision_task_location_id integer references allu.supervision_task_location(id)
);

create table allu.supervision_task_location_geometry (
  id serial primary key,
  geometry geometry(GEOMETRY, 3879),
  supervision_location_id integer references allu.supervision_task_location(id) on delete cascade 
);
