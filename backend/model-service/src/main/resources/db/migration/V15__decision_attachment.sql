alter table allu.attachment add column mime_type text default 'unknown';

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Attachment'), 'mimeType', 'Liitetiedoston tyyppi', 'STRING');