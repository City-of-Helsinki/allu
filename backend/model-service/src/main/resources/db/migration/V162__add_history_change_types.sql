insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'COMMENT_ADDED', 'Kommentti lisätty', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'COMMENT_REMOVED', 'Kommentti poistettu', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'ATTACHMENT_ADDED', 'Liite lisätty', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'ATTACHMENT_REMOVED', 'Liite poistettu', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'SUPERVISION_ADDED', 'Valvonta lisätty', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'SUPERVISION_APPROVED', 'Valvonta hyväksytty', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'SUPERVISION_REJECTED', 'Valvonta hylätty', 'ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'SUPERVISION_REMOVED', 'Valvonta poistettu', 'ENUM_VALUE');
