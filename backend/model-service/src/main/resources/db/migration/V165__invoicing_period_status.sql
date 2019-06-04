alter table allu.invoicing_period add column invoicable_status text;
alter table allu.invoicing_period alter column start_time drop not null;
alter table allu.invoicing_period alter column end_time drop not null;
