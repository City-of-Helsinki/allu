INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'), 'operationalConditionReported', 'Toiminnallisen kunnon ilmoituspäivä', 'DATETIME', null, null);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'), 'workFinishedReported', 'Valmistumisen ilmoituspäivä', 'DATETIME', null, null);

    