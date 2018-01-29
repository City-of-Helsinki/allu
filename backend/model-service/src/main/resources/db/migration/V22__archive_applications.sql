insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='StatusType'),  'ARCHIVED', 'korvattu', 'ENUM_VALUE');

alter table allu.application add column invoiced boolean;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'invoiced', 'Hakemus laskutettu', 'BOOLEAN');

