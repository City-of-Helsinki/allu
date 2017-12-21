alter table allu.application add column replaced_by_application_id integer references allu.application(id);
alter table allu.application add column replaces_application_id integer references allu.application(id);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'Application'),  'replacedByApplicationId', 'Korvaavan hakemuksen ID', 'INTEGER', null,  null);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'Application'),  'replacesApplicationId', 'Korvattavan hakemuksen ID', 'INTEGER', null, null);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='StatusType'),  'REPLACED', 'korvattu', 'ENUM_VALUE');

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='ChangeType'),  'REPLACED', 'Korvattu', 'ENUM_VALUE');
