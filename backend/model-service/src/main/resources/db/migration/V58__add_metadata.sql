insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'CUSTOMER_CHANGED', 'Asiakas päivitetty', 'ENUM_VALUE');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='ChangeType'), 'CONTACT_CHANGED', 'Yhteyshenkilö päivitetty', 'ENUM_VALUE');
