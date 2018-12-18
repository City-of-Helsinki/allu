insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='RoleType'), 'ROLE_DECLARANT', 'Lausunnonantaja', 'ENUM_VALUE');
