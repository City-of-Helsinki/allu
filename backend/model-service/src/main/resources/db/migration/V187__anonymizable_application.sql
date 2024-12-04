create table allu.anonymizable_application (
  application_id integer not null primary key references allu.application(id) on delete cascade
);
