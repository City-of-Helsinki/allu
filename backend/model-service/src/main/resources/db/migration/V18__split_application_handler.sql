alter table allu.application rename column handler to owner;
alter table allu.application rename constraint application_handler_fkey TO application_owner_fkey;

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'Application' and version = 1), 'owner', 'Hakemuksen omistaja', 'STRUCTURE', null,
        (select id from allu.structure_meta where type_name = 'User' and version = 1));
