alter table allu.customer add column project_identifier_prefix text;

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values
  ((select id from allu.structure_meta where type_name='Customer'), 'projectIdentifierPrefix', 'Projektitunnisteen alkuosa', 'STRING');
