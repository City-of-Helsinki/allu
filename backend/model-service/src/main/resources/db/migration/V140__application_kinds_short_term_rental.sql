INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='ApplicationKind'),  'SUMMER_TERRACE', 'Kesäterassi', 'ENUM_VALUE');

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='ApplicationKind'),  'WINTER_TERRACE', 'Talviterassi', 'ENUM_VALUE');

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='ApplicationKind'),  'PARKLET', 'Parklet', 'ENUM_VALUE');


