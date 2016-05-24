create table person (
    id serial primary key,
    ssn char(11),
    first_name text,
    last_name text,
    street_address text,
    zip_code text,
    city text,
    email text,
    phone text );

create table project (
    project_id serial primary key,
    owner_id integer,
    contact_id integer,
    project_name text,
    start_date date,
    end_date date,
    additional_info text );

create table application (
	application_id serial primary key,
	project_id integer references project,
	name text,
	description text,
	handler text,
	customer_id integer references person (id),
	status text,   -- TODO: enum
	type text,     -- TODO: enum
	creation_time timestamp with time zone,
	start_time timestamp with time zone );