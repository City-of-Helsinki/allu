alter table allu.attachment add column decision_attachment boolean not null default false;

update allu.attachment set decision_attachment = true where type in ('DEFAULT', 'DEFAULT_IMAGE');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Attachment'), 'decisionAttachment', 'Päätöksen liite', 'BOOLEAN');