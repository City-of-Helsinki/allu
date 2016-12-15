create table allu.applicant (
    id serial primary key,
    type text not null,
    name text not null,
    registry_key text,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table allu.fixed_location (
    id serial primary key,
    area text not null,
    section text,
    application_kind text not null,
    is_active boolean not null,
    geometry geometry(GEOMETRY, 3879),
    unique (area, section) );

comment on table allu.fixed_location is 'Predefined Area+Section type location';

create table allu.outdoor_pricing (
    id serial primary key,
    fixed_location_id integer not null references allu.fixed_location(id),
    nature text not null,              -- event's nature
    base_charge bigint not null,                -- base charge per day, in 1/100 eurocents
    build_discount_percent integer not null,    -- discount percent for build days
    duration_discount_percent integer not null, -- discount percent after N days
    duration_discount_limit integer not null,   -- day limit after which duration discount is given
    structure_extra_charges bigint[],      -- possible extra charges for structures, per 10 sqm
    structure_extra_charge_limits double precision[],  -- area limits for the structure extra charges
    area_extra_charges bigint[],           -- possible extra charges for event area, per 1 sqm
    area_extra_charge_limits double precision[]        -- area limits for the area extra charges
    );

create table allu.location (
   id serial primary key,
   street_address text,
   postal_code text,
   city text,
   area double precision,
   area_override double precision );

create table allu.location_flids (
    id serial primary key,
    location_id integer references allu.location(id),
    fixed_location_id integer references allu.fixed_location(id) );

create table allu.geometry (
   id serial primary key,
   geometry geometry(GEOMETRY, 3879),
   location_id integer references allu.location(id) );

-- TODO: reference to city districts (kaupunginosat) is missing. Add when support for districts is added elsewhere
create table allu.project (
    id serial primary key,
    parent_id integer references allu.project(id),
    name text,
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    owner_name text,
    contact_name text,
    email text,
    phone text,
    customer_reference text,
    additional_info text );

create table allu.contact (
    id serial primary key,
    applicant_id integer not null references allu.applicant(id),
    name text not null,
    street_address text,
    postal_code text,
    city text,
    email text,
    phone text );

create table allu.user (
  id serial primary key,
  user_name text NOT NULL UNIQUE,
  real_name text NOT NULL,
  email_address text NOT NULL,
  title text NOT NULL,
  is_active boolean NOT NULL);

create table allu.user_role (
  id serial primary key,
  user_id integer references allu.user(id),
  role text );

create table allu.user_application_type (
  id serial primary key,
  user_id integer references allu.user(id),
  application_type text );

create table allu.application (
    id serial primary key,
    application_id text not null,
    project_id integer references allu.project(id),
    name text,
    handler integer references allu.user,
    applicant_id integer references allu.applicant(id),
    status text,   -- TODO: enum
    type text not null,
    kind text not null,
    metadata_version integer not null,
    creation_time timestamp with time zone,
    location_id integer references allu.location(id),
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    extension text not null,
    decision_time timestamp with time zone,
    calculated_price integer,
    price_override integer,
    price_override_reason text );

create table allu.attachment (
   id serial primary key,
   application_id integer, -- TODO: references allu.application,
   name text,
   description text,
   size bigint,
   creation_time timestamp with time zone,
   data bytea );

create table allu.application_contact (
    id serial primary key,
    position integer,
    application_id integer references allu.application(id),
    contact_id integer references allu.contact(id) );

create table allu.application_billing_line (
    id serial primary key,
    application_id integer references allu.application(id),
    line_number integer,
    unit_price integer,
    amount integer,
    total_price integer,
    info_text text );

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

create table allu.cable_info_text (
    id serial primary key,
    cable_info_type text not null,
    text_value text not null );

create SEQUENCE allu.KP_application_type_sequence START 1600001;
create SEQUENCE allu.AL_application_type_sequence START 1600001;
create SEQUENCE allu.LJ_application_type_sequence START 1600001;
create SEQUENCE allu.JS_application_type_sequence START 1600001;
create SEQUENCE allu.SL_application_type_sequence START 1600001;
create SEQUENCE allu.TP_application_type_sequence START 1600001;
create SEQUENCE allu.VL_application_type_sequence START 1600001;
create SEQUENCE allu.MP_application_type_sequence START 1600001;

insert into allu.user values (DEFAULT, 'admin', 'admin user', 'admin@no-mail.fi', 'administrator', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');

insert into allu.user values (DEFAULT, 'allutest', 'all rights user', 'allutest@no-mail.fi', 'Kaikkivaltias', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_PROCESS_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_DECISION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_SUPERVISE');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_INVOICING');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
