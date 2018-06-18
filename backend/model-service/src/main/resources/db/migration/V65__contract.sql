create table allu.contract (
    id serial primary key,
    application_id integer references allu.application(id) on delete cascade,
    creation_time timestamp with time zone,
    response_time timestamp with time zone,
    proposal bytea,
    signed_contract bytea,
    status text,
    rejection_reason text
);

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES ((select id from allu.structure_meta where type_name='StatusType'),
    'WAITING_CONTRACT_APPROVAL', 'Odottaa sopimuksen hyväksyntää', 'ENUM_VALUE');
