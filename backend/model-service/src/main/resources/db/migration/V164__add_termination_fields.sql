alter table allu.termination add column termination_handler integer  references allu.user(id);
alter table allu.termination add column termination_decision_time timestamp with time zone;

