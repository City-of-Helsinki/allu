insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'), 'selfSupervision', 'Omavalvonta', 'BOOLEAN');

insert into allu.pricing (application_type, key, value) values ('EXCAVATION_ANNOUNCEMENT', 'HANDLING_FEE_SELF_SUPERVISION', 6000);
