alter table allu.configuration add column readonly boolean;
update allu.configuration set readonly = false;
alter table allu.configuration alter column readonly set not null;

update allu.configuration set readonly = true where key='PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR';

update allu.configuration set type = 'CALENDAR_DATE' where type='DATE';
