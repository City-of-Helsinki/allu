alter table allu.termination add column terminator integer references allu.user(id) on delete cascade not null;
