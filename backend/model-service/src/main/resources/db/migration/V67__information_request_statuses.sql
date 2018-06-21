insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='StatusType'),
    'WAITING_INFORMATION', 'Odottaa täydennystä', 'ENUM_VALUE');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='StatusType'),
    'INFORMATION_RECEIVED', 'Täydennys vastaanotettu', 'ENUM_VALUE');
