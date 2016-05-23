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
