create table allu.termination (
    id serial primary key,
    application_id integer references allu.application(id) on delete cascade not null,
    creation_time timestamp with time zone not null,
    document bytea,
    termination_time timestamp with time zone not null,
    reason text not null
);
