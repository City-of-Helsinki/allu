alter table allu.change_history drop constraint "change must refer to exactly one data table";
alter table allu.change_history add column project_id integer references project(id);
alter table allu.change_history add constraint history_one_type_chk
  check (((application_id is not null)::integer + (customer_id is not null)::integer + (project_id is not null)::integer) = 1);

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
  (select id from allu.structure_meta where type_name='ChangeType'),'APPLICATION_ADDED','Hakemus lisätty','ENUM_VALUE');
insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
  (select id from allu.structure_meta where type_name='ChangeType'),'APPLICATION_REMOVED','Hakemus poistettu','ENUM_VALUE');
