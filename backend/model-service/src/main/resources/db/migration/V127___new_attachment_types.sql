insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AttachmentType'), 'SUPERVISION', 'Valvontaliite', 'ENUM_VALUE');
   
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AttachmentType'), 'STATEMENT', 'Lausunto', 'ENUM_VALUE');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='AttachmentType'), 'OTHER', 'Muu liite', 'ENUM_VALUE');
