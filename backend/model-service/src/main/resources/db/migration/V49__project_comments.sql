alter table allu.application_comment rename to comment;
alter table allu.comment alter column application_id drop not null;

alter table allu.comment add column project_id integer references allu.project(id);
alter table allu.comment add constraint comment_one_type_chk
  check (((application_id is not null)::integer + (project_id is not null)::integer) = 1);