
create table allu.information_request 
(
  id serial primary key,
  application_id integer references allu.application(id),
  open boolean,  
  creation_time timestamp with time zone not null,
  creator_id integer references allu.user(id),
  unique(open, application_id)
);

create table information_request_field 
(
  id serial primary key,
  information_request_id integer references allu.information_request(id),
  field_key text,
  description text,
  unique (information_request_id, field_key)
);  

create table information_request_response_field 
(
  id serial primary key,
  information_request_id integer references allu.information_request(id),
  field_key text,
  unique (information_request_id, field_key)
);

alter table allu.external_application add information_request_id integer references allu.information_request(id);
