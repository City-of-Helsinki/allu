create table person (
    id serial primary key,
    ssn char(11),
    name text,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table organization (
    id serial primary key,
    name text,
    business_id text,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table customer (
    id serial primary key,
    type text,
    sap_id text,
    person_id integer references person(id),
    organization_id integer references organization(id));

create table project (
    id serial primary key,
    owner_id integer,
    contact_id integer,
    name text,
    start_date date,
    end_date date,
    additional_info text );

create table contact (
    id serial primary key,
    person_id integer references person(id),
    organization_id integer references organization(id));

create table applicant (
    id serial primary key,
    person_id integer references person(id),
    organization_id integer references organization(id));

create table application (
    id serial primary key,
    project_id integer references project(id),
    name text,
    handler text,
    customer_id integer references person (id),
    applicant_id integer references applicant (id),
    status text,   -- TODO: enum
    type text,     -- TODO: enum
    creation_time timestamp with time zone);

create table free_event_application (
    id serial primary key,
    application_id integer references application,
    description text,
    nature text,
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    pricing_argument text,
    external_sale_or_advertising boolean,
    eco_compass boolean,
    grocery_sale_description text,
    sale_description text,
    construction_area numeric,
    construction_description text,
    construction_start_time timestamp with time zone,
    construction_end_time timestamp with time zone);

create table attachment (
   id serial primary key,
   data bytea,
   type text,
   application_id integer references application);

