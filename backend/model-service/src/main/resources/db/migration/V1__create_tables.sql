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

create table allu.square_section (
    id serial primary key,
    square text not null,
    section text,
    is_active boolean not null,
    unique (square, section) );

comment on table allu.square_section is 'Predefined Square+Section type locations';

create table allu.outdoor_pricing (
    id serial primary key,
    square_section_id integer not null references allu.square_section(id),
    nature text not null,              -- event's nature
    base_charge bigint,                -- base charge per day, in 1/100 eurocents
    build_discount_percent integer,    -- discount percent for build days
    duration_discount_percent integer, -- discount percent after N days
    duration_discount_limit integer,   -- day limit after which duration discount is given
    structure_extra_charges bigint[],      -- possible extra charges for structures, per 10 sqm
    structure_extra_charge_limits real[],  -- area limits for the structure extra charges
    area_extra_charges bigint[],           -- possible extra charges for event area, per 1 sqm
    area_extra_charge_limits real[]        -- area limits for the area extra charges
    );

create table allu.location (
   id serial primary key,
   street_address text,
   postal_code text,
   city text,
   area double precision,
   square_section_id integer references allu.square_section(id) );

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

insert into allu.user values (DEFAULT, 'admin', 'admin user', 'admin@no-mail.fi', 'administrator', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');

insert into allu.user values (DEFAULT, 'allutest', 'all rights user', 'allutest@no-mail.fi', 'Kaikkivaltias', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_PROCESS_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_WORK_QUEUE');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_DECISION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_SUPERVISE');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_INVOICING');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'OUTDOOREVENT');

-- Insert pricing data (from http://www.hel.fi/static/hkr/luvat/maksut_tapahtumat.pdf):

insert into allu.square_section values (DEFAULT, 'Narinkka','A',true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Narinkka','B',true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Narinkka','C',true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section (square, section, is_active) values ('Narinkka','D',true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Mauno Koiviston aukio', 'E', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'A', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'B', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'C', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'D', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'E', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 3000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 9000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Rautatientori', 'F', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 3000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 9000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kansalaistori', 'A', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kansalaistori', 'B', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 7000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 14000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 21000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kansalaistori', 'C', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 4000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 8000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 12000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kaisaniemen puisto', 'A', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kaisaniemen puisto', 'B', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kaisaniemen puisto', 'C', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kaisaniemen puisto', 'D', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Töölönlahdenpuisto', 'A', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 20000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 40000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 60000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Töölönlahdenpuisto', 'B', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 15000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 45000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Töölönlahdenpuisto', 'C', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 15000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 45000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Töölönlahdenpuisto', 'D', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 25000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 50000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 75000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Kaivopuisto', NULL, true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 20000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 40000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 60000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Senaatintori', NULL, true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 60000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 90000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.square_section values (DEFAULT, 'Säiliö 468', NULL, true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_FREE', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'PUBLIC_NONFREE', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.square_section', 'id')), 'CLOSED', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL);
