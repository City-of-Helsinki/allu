alter table allu.application add column identification_number text;
insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values
  ((select id from allu.structure_meta where type_name='Application'),'identificationNumber','Asiointitunnus','STRING');

update allu.application set identification_number=extension::jsonb->>'identificationNumber' where type='PLACEMENT_CONTRACT';
update allu.application set extension = extension::jsonb - 'identificationNumber' where type='PLACEMENT_CONTRACT';
delete from allu.attribute_meta where name='identificationNumber' and structure_meta_id=(select id from allu.structure_meta where type_name='PLACEMENT_CONTRACT');
