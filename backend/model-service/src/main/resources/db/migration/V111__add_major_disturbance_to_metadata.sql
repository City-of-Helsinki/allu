insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AREA_RENTAL'), 'majorDisturbance', 'Vähäistä suurempaa haittaa aiheuttava työ', 'BOOLEAN');

update allu.pricing set key='MINOR_DISTURBANCE_HANDLING_FEE' where application_type='AREA_RENTAL' and key='SHORT_TERM_HANDLING_FEE';
update allu.pricing set key='MAJOR_DISTURBANCE_HANDLING_FEE' where application_type='AREA_RENTAL' and key='LONG_TERM_HANDLING_FEE';
