alter table allu.application add column received_time timestamp with time zone;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'receivedTime', 'Hakemus saapunut', 'DATETIME');
  
update allu.application set received_time = creation_time;