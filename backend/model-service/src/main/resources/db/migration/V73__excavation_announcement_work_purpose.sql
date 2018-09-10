update allu.application set extension = extension::jsonb - 'additionalInfo' || jsonb_build_object('workPurpose', extension::jsonb->'additionalInfo') where type='EXCAVATION_ANNOUNCEMENT';
update allu.attribute_meta set name='workPurpose',ui_name='Työn tarkoitus' where name='additionalInfo' and structure_meta_id in (select id from structure_meta where type_name='EXCAVATION_ANNOUNCEMENT');
