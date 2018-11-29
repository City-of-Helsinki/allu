create table allu.customer_location_validity (
  id serial primary key,
  location_id integer not null references allu.location(id),
  start_time timestamp with time zone,
  end_time timestamp with time zone,
  reporting_time timestamp with time zone
);

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Location'), 'customerStartTime', 'Asiakkaan ilmoittama alkuaika', 'DATETIME');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Location'), 'customerEndTime', 'Asiakkaan ilmoittama loppuaika', 'DATETIME');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Location'), 'customerReportingTime', 'Voimassaolo ilmoitettu', 'DATETIME');
