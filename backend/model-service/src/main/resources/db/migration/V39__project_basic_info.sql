do
$$

declare tmp_customer_id customer.id%type;
declare tmp_contact_id contact.id%type;

begin
  insert into allu.customer (type,name,is_active,invoicing_prohibited,invoicing_only,country_id) values ('OTHER','Väliaikaisasiakas',false,true,false,68) returning id into tmp_customer_id;
  insert into allu.contact (customer_id,name,is_active) values (tmp_customer_id,'Väliaikaisyhteyshenkilö',false) returning id into tmp_contact_id;

  alter table allu.project drop column owner_name,
                           drop column contact_name,
                           drop column email,
                           drop column phone;
  alter table allu.project add column customer_id integer references allu.customer(id);
  alter table allu.project add column contact_id integer references allu.contact(id);

  update allu.project set customer_id=tmp_customer_id;
  update allu.project set contact_id=tmp_contact_id;

  alter table allu.project alter column customer_id set not null;
  alter table allu.project alter column contact_id set not null;

  alter table allu.project add column identifier text;
  create temporary sequence allu_project_identifier_sequence;
  update allu.project  set identifier = 'projekti' || nextval('allu_project_identifier_sequence') where identifier is null;
  alter table allu.project alter column identifier set not null;
  alter table allu.project add constraint identifier_unique unique(identifier);
end;
$$

language plpgsql;
