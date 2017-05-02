create table allu.postal_address (
    id serial primary key,
    street_address text,
    postal_code text,
    city text );

create table allu.applicant (
    id serial primary key,
    postal_address_id integer references allu.postal_address(id),
    type text not null,
    name text not null,
    registry_key text,
    email text,
    phone text,
    is_active boolean not null );

create table allu.city_district (
  id serial primary key,
  district_id integer not null unique,
  name text,
  geometry geometry(GEOMETRY, 3879),
  zone_id integer);

create table allu.project (
    id serial primary key,
    parent_id integer references allu.project(id),
    name text,
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    city_districts integer[],         -- references to allu.city_district(id)
    owner_name text,
    contact_name text,
    email text,
    phone text,
    customer_reference text,
    additional_info text );

create table allu.contact (
    id serial primary key,
    applicant_id integer not null references allu.applicant(id),
    postal_address_id integer references allu.postal_address(id),
    name text not null,
    email text,
    phone text,
    is_active boolean not null );

create table allu.user (
  id serial primary key,
  user_name text NOT NULL UNIQUE,
  real_name text NOT NULL,
  email_address text,
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

create table allu.user_city_district (
  id serial primary key,
  user_id integer references allu.user(id) not null,
  city_district_id integer references allu.city_district(id) not null
);

create table allu.application (
    id serial primary key,
    application_id text unique not null,
    project_id integer references allu.project(id),
    name text,
    handler integer references allu.user,
    applicant_id integer references allu.applicant(id),
    status text,   -- TODO: enum
    type text not null,
    kind text not null,
    metadata_version integer not null,
    creation_time timestamp with time zone,
    start_time timestamp with time zone,
    end_time timestamp with time zone,
    extension text not null,
    decision_distribution_type text not null,
    decision_publicity_type text not null,
    decision_time timestamp with time zone,
    decision_maker integer references allu.user,
    calculated_price integer,
    price_override integer,
    price_override_reason text );

create table allu.distribution_entry (
  id serial primary key,
  application_id integer references allu.application(id) not null,
  postal_address_id integer references allu.postal_address(id),
  distribution_type text not null,
  name text,
  email text
);

create table allu.application_tag (
    id serial primary key,
    application_id integer not null references allu.application(id),
    added_by integer references allu.user(id),
    type text not null,
    creation_time timestamp with time zone not null
);

create table allu.location_area (
  id serial primary key,
  name text not null );

create table allu.fixed_location (
  id serial primary key,
  area_id integer not null references allu.location_area(id),
  section text,
  application_kind text not null,
  is_active boolean not null,
  geometry geometry(GEOMETRY, 3879),
unique (area_id, section, application_kind) );

comment on table allu.fixed_location is 'Predefined Area+Section type location';

create table allu.location (
  id serial primary key,
  application_id integer not null references allu.application(id),
  location_key integer not null,            -- human readable name for location. Each location has names from 1 to number of locations
  location_version integer not null,        -- version of the location with same location_key. If area changes, it will be stored as new version
  start_time timestamp with time zone not null,
  end_time timestamp with time zone not null,
  postal_address_id integer references allu.postal_address(id),
  area double precision,
  area_override double precision,
  city_district_id integer references allu.city_district(id),
  city_district_id_override integer references allu.city_district(id),
  payment_tariff integer,                   -- the payment tariff (maksuluokka) of the location
  payment_tariff_override integer,          -- possible user defined override for the payment tariff
  underpass boolean not null,               -- altakuljettava i.e. it's possible to pass through the reserved area without obstacles
  unique (application_id, location_key, location_version));

create table allu.location_flids (
  id serial primary key,
  location_id integer references allu.location(id),
  fixed_location_id integer references allu.fixed_location(id) );

create table allu.location_geometry (
  id serial primary key,
  geometry geometry(GEOMETRY, 3879),
  location_id integer references allu.location(id) );

create table allu.outdoor_pricing (
  id serial primary key,
  fixed_location_id integer references allu.fixed_location(id),
  zone_id integer,
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

create table allu.attachment (
   id serial primary key,
   user_id integer references allu.user(id),
   type text not null,
   name text,
   description text,
   size bigint,
   creation_time timestamp with time zone,
   data bytea );

create table allu.application_attachment (
  id serial primary key,
  application_id integer not null references allu.application(id),
  attachment_id integer not null references allu.attachment(id)
);

create table allu.default_attachment (
    id serial primary key,
    attachment_id integer not null unique references allu.attachment(id),
    deleted boolean not null,
    location_area_id integer references allu.location_area(id)
);

create table allu.default_attachment_application_type (
  id serial primary key,
  default_attachment_id integer not null references allu.default_attachment(id),
  application_type text not null
);

create table allu.application_contact (
    id serial primary key,
    position integer,
    application_id integer references allu.application(id),
    contact_id integer references allu.contact(id) );

create table allu.invoice_row (
    id serial primary key,
    application_id integer references allu.application(id) not null,
    row_number integer not null,
    manually_set boolean not null,
    unit text not null,
    quantity double precision not null,
    row_text text not null,
    unit_price integer not null,
    net_price integer not null);

create table allu.structure_meta (
    id serial primary key,
    type_name text not null,
    version integer not null, -- when the structure was last changed
    unique (type_name, version) );

create table allu.attribute_meta (
    id serial primary key,
    structure_meta_id integer references allu.structure_meta(id),
    name text not null,
    ui_name text not null,
    data_type text not null,
    list_type text,
    structure_attribute integer references allu.structure_meta(id),
    unique (structure_meta_id, name) );

create table allu.decision (
    id serial primary key,
    application_id integer references allu.application(id),
    creation_time timestamp with time zone,
    data bytea,
    decision_time timestamp with time zone,
    status text );

create table allu.default_text (
    id serial primary key,
    application_type text not null,
    text_type text not null,
    text_value text not null );

create table allu.application_comment (
    id serial primary key,
    application_id integer references allu.application(id) not null,
    user_id integer references allu.user(id) not null,
    type text not null,
    text text not null,
    create_time timestamp with time zone not null,
    update_time timestamp with time zone not null);

create table allu.application_change (
    id serial primary key,
    application_id integer references allu.application(id) not null,
    user_id integer references allu.user(id) not null,
    change_type text not null,   -- change type
    new_status text,      -- new status if applicable
    change_time timestamp with time zone not null);

create table allu.application_field_change (
    id serial primary key,
    application_change_id integer references allu.application_change(id) not null,
    field_name text not null,
    old_value text,
    new_value text);

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

insert into allu.user values (DEFAULT, 'ALLUTE', 'all rights user', 'allutest@no-mail.fi', 'Kaikkivaltias', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_PROCESS_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_DECISION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_SUPERVISE');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_INVOICING');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_ADMIN');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.user values (DEFAULT, 'ALLUASPA', 'Allu Asiakaspalvelija', 'allu.asiakaspalvelija@no-mail.fi', 'Asiakaspalvelija', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.user values (DEFAULT, 'ALLUKASI', 'Allu Käsittelijä', 'allu.kasittelija@no-mail.fi', 'Käsittelijä', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_PROCESS_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.user values (DEFAULT, 'ALLUPAAT', 'Allu Päättäjä', 'allu.paattaja@no-mail.fi', 'Päättäjä', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_CREATE_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_PROCESS_APPLICATION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_DECISION');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.user values (DEFAULT, 'ALLUVALV', 'Allu Valvoja', 'allu.valvoja@no-mail.fi', 'Valvoja', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_SUPERVISE');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.user values (DEFAULT, 'ALLULASK', 'Allu Laskuttaja', 'allu.laskuttaja@no-mail.fi', 'Laskuttaja', true);
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_INVOICING');
insert into allu.user_role values (DEFAULT , currval(pg_get_serial_sequence('allu.user', 'id')), 'ROLE_VIEW');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EXCAVATION_ANNOUNCEMENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'AREA_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'TEMPORARY_TRAFFIC_ARRANGEMENTS');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'CABLE_REPORT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'PLACEMENT_CONTRACT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'EVENT');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'SHORT_TERM_RENTAL');
insert into allu.user_application_type values (DEFAULT, currval(pg_get_serial_sequence('allu.user', 'id')), 'NOTE');

insert into allu.default_text (application_type, text_type, text_value) values
  ('CABLE_REPORT', 'ELECTRICITY', 'Sijainti johtokartalla epävarma.'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Kohteessa 10, 20, 30kV kaapeli.'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Kohteessa suurjännitekaapeli 110kV.'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Tilattava näyttö. puh. 040 763 6626'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Sijainti johtokartalla osittain epävarma.'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Kartassa saattaa esiintyä puutteita, otettava yhteys näyttäjään puh. 040 763 6626'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Otettava yhteys liikennevalo-ohjauskeskukseen. Päivystys puh: 310 37975 ksv.liikenteenohjaus@hel.fi'),
  ('CABLE_REPORT', 'ELECTRICITY', 'Poliisin päivystys: Ark. 6-23 Su. 7-23 Puh: 310 37555'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Sijainti johtokartalla epävarma.'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Sijainti johtokartalla osittain epävarma.'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Tilattava näyttö.'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Johtotieto Oy 0800 12 600'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Relacom 0800 133 544'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Otettava yhteys Johtotieto Oy: puh. 0800 12600 !'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Otettava yhteys Johtotieto Oy: puh. 0800 12600 ! (Elisa)'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Otettava yhteys Johtotieto Oy: puh. 0800 12600 ! (TeliaSonera)'),
  ('CABLE_REPORT', 'TELECOMMUNICATION', 'Otettava yhteys Johtotieto Oy: puh. 0800 12600 ! (DNA)'),
  ('CABLE_REPORT', 'WATER_AND_SEWAGE', 'Sijainti johtokartalla epävarma.'),
  ('CABLE_REPORT', 'WATER_AND_SEWAGE', 'Sijainti johtokartalla osittain epävarma.'),
  ('CABLE_REPORT', 'WATER_AND_SEWAGE', 'Tilattava näyttö.'),
  ('CABLE_REPORT', 'DISTRICT_HEATING_COOLING', 'Sijainti johtokartalla epävarma.'),
  ('CABLE_REPORT', 'DISTRICT_HEATING_COOLING', 'Sijainti johtokartalla osittain epävarma.'),
  ('CABLE_REPORT', 'DISTRICT_HEATING_COOLING', 'Tilattava näyttö.'),
  ('CABLE_REPORT', 'GAS', 'Otettava yhteyttä Aurora Kaasunjakelu Oy ennen työn aloittamista. Puh. 0800 122 722.'),
  ('CABLE_REPORT', 'GAS', 'Otettava yhteyttä Gasum Oy:hyn ennen työn aloittamista. Puh. 0800 122 722'),
  ('CABLE_REPORT', 'GAS', 'Sijainti johtokartalla epävarma.'),
  ('CABLE_REPORT', 'GAS', 'Sijainti johtokartalla osittain epävarma.'),
  ('CABLE_REPORT', 'UNDERGROUND_STRUCTURE', ' Sijainti kartalla epävarma.'),
  ('CABLE_REPORT', 'UNDERGROUND_STRUCTURE', 'Maanalaisen yleiskaavaluonnoksen mukainen varaus.'),
  ('CABLE_REPORT', 'UNDERGROUND_STRUCTURE', 'Porakaivon etäisyys tunnelista tulee olla vähintään 20m.'),
  ('CABLE_REPORT', 'UNDERGROUND_STRUCTURE', 'Selvitettävä kiinteistön omat maanalaiset tilat esim. isännöitsijältä'),
  ('CABLE_REPORT', 'UNDERGROUND_STRUCTURE', 'Korkotiedot liitteenä.'),
  ('CABLE_REPORT', 'TRAMWAY', 'Työskenneltäessä 2m lähempänä kiskoja tai ajojohtimia otettava yhteys HKL:ään. Puh: 310 35413'),
  ('CABLE_REPORT', 'SEWAGE_PIPE', 'Ota tarvittaessa yhteyttä: Caverion Oy puh: 010 4079780 kiinteistovalvomo@caverion.fi'),
  ('CABLE_REPORT', 'GEOTECHNICAL_OBSERVATION_POST', 'Geoteknisen osaston pysyvä tarkkailupiste.'),
  ('CABLE_REPORT', 'OTHER', 'Nykyiset ja entiset VR:n alueet: Otettava aina yhteys Liikennevirastoon ja Eltel Networks Oy:n näyttäjään puh. 040 311 4459'),
  ('CABLE_REPORT', 'OTHER', 'Liitosalueella vesihuolto näytöt tekee Johtotieto Oy. puh. 044 587 1114.'),
  ('CABLE_REPORT', 'OTHER', 'Liitosalueella sähköverkon näytöt tekee Kaivulupa.fi. puh. 0800 133 544'),
  ('CABLE_REPORT', 'OTHER', 'Kiinteistön alueella sijaitsevissa johdoissa puutteita.');
