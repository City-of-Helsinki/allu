create table allu.invoice_recipient (
    id serial primary key,
    type text not null,
    name text not null,
    registry_key text,
    ovt text,
    email text,
    phone text,
    street_address text,
    postal_code text,
    city text);

alter table allu.invoice add column recipient_id integer references allu.invoice_recipient(id);

do
$$
declare
    r record;
    invoice_recipient_id integer;
begin
    for r in
        select i.id,c.type,c.name,c.registry_key,c.ovt,c.email,c.phone,p.street_address,p.postal_code,p.city from allu.invoice i,allu.application a,allu.customer c,allu.postal_address p where i.application_id=a.id and a.invoice_recipient_id=c.id and c.postal_address_id=p.id
    loop
        invoice_recipient_id:= nextval(pg_get_serial_sequence('allu.invoice_recipient', 'id'));
        insert into allu.invoice_recipient (id,type,name,registry_key,ovt,email,phone,street_address,postal_code,city) values (invoice_recipient_id,r.type,r.name,r.registry_key,r.ovt,r.email,r.phone,r.street_address,r.postal_code,r.city);
        update allu.invoice set recipient_id = invoice_recipient_id where id = r.id ;
    end loop;
end;
$$
LANGUAGE plpgsql;

alter table allu.invoice alter column recipient_id set not null;
