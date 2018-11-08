insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AREA_RENTAL'), 'customerWorkFinished', 'Asiakkaan ilmoittama aika, jolloin työ on valmis', 'DATETIME');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AREA_RENTAL'), 'workFinishedReported', 'Valmistumisen ilmoituspäivä', 'DATETIME');
