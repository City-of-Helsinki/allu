-- Add registrationNumbers attribute metadata for SHORT_TERM_RENTAL extension
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'SHORT_TERM_RENTAL'), 'registrationNumbers', 'Rekisterinumerot', 'STRING', null, null);
