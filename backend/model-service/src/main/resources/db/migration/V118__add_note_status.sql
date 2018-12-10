insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='StatusType'),
    'NOTE', 'Muistiinpano', 'ENUM_VALUE');

update allu.application set status = 'NOTE' where type = 'NOTE';