alter table allu.application add column external_application_id integer;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'externalApplicationId', 'Hakemuksen ulkoinen ID', 'INTEGER');
