create table allu.external_application
(
  application_id integer references allu.application(id),
  application_data text
);
