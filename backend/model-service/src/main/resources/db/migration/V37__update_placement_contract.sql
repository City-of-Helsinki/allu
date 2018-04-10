insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
  (select id from allu.structure_meta where type_name='PLACEMENT_CONTRACT'),'propertyIdentificationNumber','Kiinteistötunnus','STRING');

update allu.attribute_meta set ui_name='Sopimusteksti',name='contractText' where name='generalTerms' and structure_meta_id=(select id from allu.structure_meta where type_name='PLACEMENT_CONTRACT');
update allu.application set extension = extension::jsonb - 'generalTerms' || jsonb_build_object('contractText', extension::jsonb->'generalTerms');

update allu.attribute_meta set ui_name='Asiointitunnus',name='identificationNumber' where name='diaryNumber' and structure_meta_id=(select id from allu.structure_meta where type_name='PLACEMENT_CONTRACT');
update allu.application set extension = extension::jsonb - 'diaryNumber' || jsonb_build_object('identificationNumber', extension::jsonb->'diaryNumber');
