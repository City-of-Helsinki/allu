update allu.application set invoiced=false where invoiced is null;
alter table allu.application alter column invoiced set default false;
alter table allu.application alter column invoiced set not null;

update allu.application set skip_price_calculation=false where skip_price_calculation is null;
alter table allu.application alter column skip_price_calculation set default false;
alter table allu.application alter column skip_price_calculation set not null;

update allu.stored_filter set default_filter=false where default_filter is null;
alter table allu.stored_filter alter column default_filter set default false;
alter table allu.stored_filter alter column default_filter set not null;
