INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='ApplicationKind'),  'BIG_EVENT', 'Suuret tapahtumat', 'ENUM_VALUE');


