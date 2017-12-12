create schema if not exists mobilenote;

drop view if exists mobilenote.application;
create view mobilenote.application as
with kind as -- appl.id --> application kinds
 (select application_id, string_agg(kind, ', ') as kinds
  from allu.application_kind
  group by application_id)
, geo as  -- appl.id --> area & geometries
 (select loc.application_id,
    sum(coalesce(loc.area_override, loc.area)) as area,
    ST_Union(geom.geometry) as geometry
  from allu.location as loc, allu.location_geometry as geom
  where loc.id = geom.location_id
  group by application_id)
, sectionlist as -- location_id -> name & section
 (select location_flids.location_id,
    location_area.name as area_name,
    fixed_location.section as section
  from allu.location_flids, allu.fixed_location, allu.location_area
  where location_flids.fixed_location_id = fixed_location.id
    and fixed_location.area_id = location_area.id
  order by location_id, section)
, location_names as -- location_id -> name & list of sections
 (select location_id, area_name, string_agg(section, ', ') as sections
  from sectionlist
  group by location_id, area_name)
, addresses as
 (select id, concat_ws(', ', street_address, postal_code, city) as address
  from allu.postal_address)
, loclist as -- appl.id -> location names & sections
 (select loc.application_id as application_id,
    string_agg(nullif(trim(loc.additional_info), ''), ' / ') as address_additional_info,
    string_agg(nullif(trim(locnam.area_name), ''), ' / ') as location_name,
    string_agg(nullif(trim(locnam.sections), ''), ' / ') as location_sections,
    string_agg(nullif(trim(addr.address), ''), ' / ') as address
  from allu.location loc left join location_names locnam
  on loc.id = locnam.location_id
  join addresses addr on loc.postal_address_id = addr.id
  group by application_id)
, customer as -- appl.id -> customers & contacts by role type
 (select ac.application_id as application_id,
    string_agg(concat_ws(', ', c.name, c.phone, c.email), ' / ') as customers,
    string_agg(concat_ws(', ', ct.name, ct.phone, ct.email), ' / ') as contacts,
    ac.customer_role_type as role_type
  from allu.application_customer ac
  join allu.customer c on ac.customer_id = c.id
  join allu.application_customer_contact acc
  on acc.application_customer_id = ac.id
  join allu.contact ct on acc.contact_id = ct.id
  group by application_id, role_type)
, com as -- single comments one per row
 (select application_id,
    concat_ws(': ', type, text) as comment
  from allu.application_comment
  order by create_time)
, comments as -- all application's comments on one row per application
(select application_id,
   string_agg(comment, ' / ') as comments
 from com
 group by application_id)
select appl.id as id,
geo.geometry as geometry,
appl.project_id as project_id,
appl.application_id as application_id,
appl.type as application_type,
kind.kinds as application_kind,
appl.status as application_status,
appl.name as application_name,
appl.extension ::jsonb ->> 'description' as description,
loclist.address as address,
loclist.address_additional_info as address_additional_info,
loclist.location_name as location_name,
loclist.location_sections as location_sections,
appl.start_time as start_time,
appl.end_time as end_time,
applicant.customers as applicant,
applicant.contacts as applicant_contact,
repr.customers as representative,
repr.contacts as representative_contact,
geo.area as area,
         not appl.not_billable as is_billable,
comms.comments as comments,
appl.extension ::jsonb ->> 'nature' as event_nature,
appl.extension ::jsonb ->> 'structureArea' as event_structure_area,
appl.extension ::jsonb ->> 'structureDescription' as event_structure_description,
appl.extension ::jsonb ->> 'marketingProviders' as event_sales_activities_description,
appl.extension ::jsonb ->> 'foodProviders' as event_food_sales_description,
appl.start_time + cast(appl.extension ::jsonb ->> 'buildSeconds' as int) * interval '1 second' as event_start_time,
appl.end_time - cast(appl.extension ::jsonb ->> 'teardownSeconds' as int) * interval '1 second' as event_end_time,
appl.extension ::jsonb ->> 'timeExceptions' as event_time_exceptions,
appl.extension ::jsonb ->> 'url' as event_web_page
from allu.application appl
join kind on appl.id = kind.application_id
left join geo on appl.id = geo.application_id
left join loclist on appl.id = loclist.application_id
left join customer as applicant
 on appl.id = applicant.application_id and applicant.role_type = 'APPLICANT'
left join customer as repr
 on appl.id = repr.application_id and repr.role_type = 'REPRESENTATIVE'
left join comments as comms
 on appl.id = comms.application_id;

-- projektit
drop view if exists mobilenote.project;
create view mobilenote.project as
with geo as  -- appl.id --> area & geometries
 (select loc.application_id,
    sum(coalesce(loc.area_override, loc.area)) as area,
    ST_Union(geom.geometry) as geometry
  from allu.location as loc, allu.location_geometry as geom
  where loc.id = geom.location_id
  group by application_id)
, apps as
 (select
   app.project_id as project_id,
   string_agg(cast(app.id as text), ', ') as ids,
   ST_Union(geo.geometry) as geometries
  from allu.application as app
  left join geo on app.id = geo.application_id
  where project_id is not null
  group by project_id)
select p.id as id,
  apps.geometries as geometry,
  apps.ids as application_ids,
  p.owner_name as project_owner,
  concat_ws(p.contact_name, p.email, p.phone, ', ') as project_contact,
  p.name as project_name,
  p.additional_info as additional_info,
  '<COMMENTS>' as comments
from allu.project as p
left join apps on p.id = apps.project_id;

-- supervision task
drop view if exists mobilenote.supervision_task;
create view mobilenote.supervision_task as
select st.id,
  st.type as activity,
  st.planned_finishing_time as time,
  concat_ws(', ', u.real_name, u.email_address) as supervisor,
  st.description,
  st.application_id
from allu.supervision_task as st join
allu.user as u on st.handler_id = u.id;
