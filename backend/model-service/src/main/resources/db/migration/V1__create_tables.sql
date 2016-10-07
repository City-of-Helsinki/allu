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

create table allu.location (
   id serial primary key,
   street_address text,
   postal_code text,
   city text,
   area double precision );

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
    application_id text not null,
    project_id integer references allu.project(id),
    name text,
    handler text,
    applicant_id integer references allu.applicant(id),
    status text,   -- TODO: enum
    type text not null,
    metadata_version integer not null,
    creation_time timestamp with time zone,
    location_id integer references allu.location(id),
    start_time timestamp with time zone,
    end_time timestamp with time zone,
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

create table allu.decision (
    id serial primary key,
    application_id integer references allu.application(id),
    creation_time timestamp with time zone,
    data bytea,
    decision_time timestamp with time zone,
    status text );

create SEQUENCE allu.KP_application_type_sequence START 1600001;
create SEQUENCE allu.AL_application_type_sequence START 1600001;
create SEQUENCE allu.LJ_application_type_sequence START 1600001;
create SEQUENCE allu.JS_application_type_sequence START 1600001;
create SEQUENCE allu.SL_application_type_sequence START 1600001;
create SEQUENCE allu.TP_application_type_sequence START 1600001;
create SEQUENCE allu.VL_application_type_sequence START 1600001;
create SEQUENCE allu.MP_application_type_sequence START 1600001;

create table allu.user (
    id serial primary key,
    user_name text NOT NULL UNIQUE,
    real_name text NOT NULL,
    email_address text NOT NULL,
    title text NOT NULL,
    is_active boolean );

create table allu.user_role (
    id serial primary key,
    user_id integer references allu.user(id),
    role text );

create table allu.user_application_type (
    id serial primary key,
    user_id integer references allu.user(id),
    application_type text );

insert into allu.user values (DEFAULT, 'admin', 'admin user', 'no@mail.fi', 'administrator', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');
