update allu.application set extension = extension::jsonb || jsonb_build_object('compactionAndBearingCapacityMeasurement', 'false') where type='EXCAVATION_ANNOUNCEMENT';
update allu.application set extension = extension::jsonb || jsonb_build_object('qualityAssuranceTest', 'false') where type='EXCAVATION_ANNOUNCEMENT';

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
    (select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'),'compactionAndBearingCapacityMeasurement','Tiiveys- ja kantavuusmittaus','BOOLEAN');

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
    (select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'),'qualityAssuranceTest','Päällysteen laadunvarmistuskoe','BOOLEAN');
