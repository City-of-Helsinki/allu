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
    type text not null,
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
    type text not null,
    person_id integer references allu.person(id),
    organization_id integer references allu.organization(id));

create table allu.application (
    id serial primary key,
    project_id integer references allu.project(id),
    name text,
    handler text,
    customer_id integer references allu.customer(id),
    applicant_id integer references allu.applicant(id),
    status text,   -- TODO: enum
    type text not null,
    creation_time timestamp with time zone,
    location_id integer references allu.location(id),
    event text not null);

create table allu.attachment (
   id serial primary key,
   data bytea,
   type text,
   application_id integer references allu.application);
