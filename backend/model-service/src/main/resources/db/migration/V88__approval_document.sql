create table allu.approval_document (
    id serial primary key,
    application_id integer references allu.application(id) on delete cascade not null,
    creation_time timestamp with time zone not null,
    document bytea not null,
    type text not null,
    unique(application_id, type)
);
