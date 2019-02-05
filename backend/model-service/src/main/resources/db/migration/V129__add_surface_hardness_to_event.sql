INSERT INTO allu.structure_meta (type_name, version) VALUES ('SurfaceHardness', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SOFT', 'Pehmeä pinta', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'HARD', 'Kova pinta', 'ENUM_VALUE');

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name='EVENT'), 'surfaceHardness', 'Pinnan tyyppi', 'ENUMERATION', null,
    (select id from allu.structure_meta where type_name = 'SurfaceHardness' and version = 1));

UPDATE application SET extension = extension::jsonb || jsonb_build_object('surfaceHardness', 'HARD')
 WHERE type = 'EVENT'
 AND status IN ('PRE_RESERVED', 'PENDING', 'WAITING_INFORMATION', 'INFORMATION_RECEIVED', 'HANDLING', 'RETURNED_TO_PREPARATION');