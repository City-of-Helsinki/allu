-- Remove unused ownerName field
delete from attribute_meta where structure_meta_id = (select id from structure_meta where type_name = 'Project') and name = 'ownerName';

-- Add missing fields
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Project'), 'customerId', 'Asiakkaan tunniste', 'INTEGER');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Project'), 'contactId', 'Yhteyshenkilön tunniste', 'INTEGER');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Project'), 'identifier', 'Hanketunnus', 'STRING');
