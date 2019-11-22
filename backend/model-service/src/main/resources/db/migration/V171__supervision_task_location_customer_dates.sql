alter table allu.supervision_task_location
add column customer_start_time timestamp with time zone,
add column customer_end_time timestamp with time zone,
add column customer_reporting_time timestamp with time zone;
