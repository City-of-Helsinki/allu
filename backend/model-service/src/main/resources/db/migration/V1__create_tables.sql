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
   street_address text,
   postal_code text,
   city text );

create table allu.geometry (
   id serial primary key,
   geometry geometry(GEOMETRY, 3879),
   location_id integer references allu.location(id) );

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
    organization_id integer not null references allu.organization(id),
    name text not null,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

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
    metadata_version integer not null,
    creation_time timestamp with time zone,
    location_id integer references allu.location(id),
    event text not null,
    decision_time timestamp with time zone);

create table allu.attachment (
   id serial primary key,
   application_id integer, -- TODO: references allu.application,
   name text,
   description text,
   size bigint,
   creation_time timestamp with time zone,
   data bytea );

create table allu.project_contact (
    id serial primary key,
    position integer,
    project_id integer references allu.project(id),
    contact_id integer references allu.contact(id) );

create table allu.application_contact (
    id serial primary key,
    position integer,
    application_id integer references allu.application(id),
    contact_id integer references allu.contact(id) );

create table allu.structure_meta (
    id serial primary key,
    application_type text not null,
    version integer not null); -- TOOD: application_type + version = unique

create table allu.attribute_meta (
    id serial primary key,
    structure integer references allu.structure_meta(id),
    name text not null, -- TODO: structure + name = unique
    ui_name text not null,
    data_type text not null,
    list_type text,
    structure_attribute integer references allu.structure_meta(id),
    validation_rule text);
