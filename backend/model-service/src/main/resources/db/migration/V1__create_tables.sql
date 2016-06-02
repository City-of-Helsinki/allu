create table allu.person (
    id serial primary key,
    ssn char(11),
    name text,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table allu.organization (
    id serial primary key,
    name text,
    business_id text,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table allu.customer (
    id serial primary key,
    type text,
    sap_id text,
    person_id integer references allu.person(id),
    organization_id integer references allu.organization(id));

create table allu.location (
   id serial primary key,
   geometry geometry(geometrycollection, 3879),
   street_address text,
   postal_code text,
   city text );

create table allu.project (
    id serial primary key,
    owner_id integer,
    contact_id integer,
    name text,
    start_date date,
    end_date date,
    additional_info text );

create table allu.contact (
    id serial primary key,
    person_id integer references allu.person(id),
    organization_id integer references allu.organization(id));

create table allu.applicant (
    id serial primary key,
    person_id integer references allu.person(id),
    organization_id integer references allu.organization(id));

create table allu.application (
    id serial primary key,
    project_id integer references allu.project(id),
    name text,
    handler text,
    customer_id integer references allu.person(id),
    applicant_id integer references allu.applicant(id),
    status text,   -- TODO: enum
    type text,     -- TODO: enum
    creation_time timestamp with time zone,
    location_id integer references allu.location(id));

create table allu.free_event_application (
    id serial primary key,
    application_id integer references allu.application,
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

create table allu.attachment (
   id serial primary key,
   data bytea,
   type text,
   application_id integer references allu.application);
