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
    id serial primary key,
    name text,
    start_date date,
    end_date date,
    additional_info text,
    contact_person int );
