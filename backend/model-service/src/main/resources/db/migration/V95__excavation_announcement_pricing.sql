create table allu.pricing (
  id serial primary key,
  application_type text not null,
  key text not null,
  payment_class text,
  value integer not null,
  unique(application_type, key, payment_class));

insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'HANDLING_FEE', null, 18000);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'SMALL_AREA_DAILY_FEE', '1', 5000);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'SMALL_AREA_DAILY_FEE', '2', 2500);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'SMALL_AREA_DAILY_FEE', '3', 1250);

insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'MEDIUM_AREA_DAILY_FEE', '1', 6500);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'MEDIUM_AREA_DAILY_FEE', '2', 3250);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'MEDIUM_AREA_DAILY_FEE', '3', 1625);

insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'LARGE_AREA_DAILY_FEE', '1', 8000);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'LARGE_AREA_DAILY_FEE', '2', 4000);
insert into allu.pricing (application_type, key, payment_class, value) values ('EXCAVATION_ANNOUNCEMENT', 'LARGE_AREA_DAILY_FEE', '3', 2000);
