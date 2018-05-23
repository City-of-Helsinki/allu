update allu.application set extension = extension::jsonb - 'pksCard' where type='TEMPORARY_TRAFFIC_ARRANGEMENTS';
delete from allu.attribute_meta where name='pksCard' and structure_meta_id in (select id from structure_meta where type_name='TEMPORARY_TRAFFIC_ARRANGEMENTS');
