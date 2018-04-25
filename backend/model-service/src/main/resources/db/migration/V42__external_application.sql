insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='StatusType'),  'PENDING_CLIENT', 'Vireillä asiakasjärjestelmässä', 'ENUM_VALUE');

alter table allu.application add column external_owner_id integer references allu.external_user(id);

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'externalOwnerId', 'Hakemuksen ulkoinen omistaja', 'INTEGER');

alter table allu.application add column client_application_data text;
    
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'clientApplicationData', 'Asiakasjärjestelmän hakemusdata', 'STRING');