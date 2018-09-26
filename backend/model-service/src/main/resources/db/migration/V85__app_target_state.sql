alter table allu.application add column target_state text;
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type, structure_attribute) values
  ((select id from allu.structure_meta where type_name = 'Application'), 'targetState', 'Kohdetila', 'ENUMERATION', (select id from allu.structure_meta where type_name = 'StatusType'));