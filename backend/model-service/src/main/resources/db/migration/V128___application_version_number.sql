alter table allu.application add column version integer not null default 1;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'version', 'Hakemuksen versionumero', 'INTEGER');


