insert into allu.configuration (type, value, key) values ('USER', '', 'EXCAVATION_ANNOUNCEMENT_DECISION_MAKER');
insert into allu.configuration (type, value, key) values ('USER', '', 'AREA_RENTAL_DECISION_MAKER');
insert into allu.configuration (type, value, key) values ('USER', '', 'TEMPORARY_TRAFFIC_ARRANGEMENTS_DECISION_MAKER');
insert into allu.configuration (type, value, key) select 'USER', '', 'PLACEMENT_CONTRACT_DECISION_MAKER'
    where not exists (select id from allu.configuration where type='USER' and key='PLACEMENT_CONTRACT_DECISION_MAKER');
insert into allu.configuration (type, value, key) values ('USER', '', 'EVENT_DECISION_MAKER');
insert into allu.configuration (type, value, key) values ('USER', '', 'SHORT_TERM_RENTAL_DECISION_MAKER');
