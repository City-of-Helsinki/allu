-- Add noAreaUsageFee metadata for EXCAVATION_ANNOUNCEMENT extension
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'EXCAVATION_ANNOUNCEMENT'), 'noAreaUsageFee', 'Ei peritä alueenkäyttömaksua', 'BOOLEAN', null, null),
           ((select id from allu.structure_meta where type_name = 'EXCAVATION_ANNOUNCEMENT'), 'noAreaUsageFeeReason', 'Peruste', 'STRING', null, null);
