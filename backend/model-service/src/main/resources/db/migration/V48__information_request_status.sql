alter table allu.information_request add column status text default 'OPEN';
alter table allu.information_request alter column status set not null;
alter table allu.information_request drop column open;

alter table allu.external_application 
add constraint appid_requestid_unique unique(application_id, information_request_id);
