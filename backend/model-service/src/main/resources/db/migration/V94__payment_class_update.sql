alter table allu.payment_class add column new_payment_class text;
update allu.payment_class set new_payment_class=payment_class::text;
alter table allu.payment_class drop column payment_class;
alter table allu.payment_class rename column new_payment_class to payment_class;
alter table allu.payment_class alter column payment_class set not null;

alter table allu.location add column new_payment_tariff text;
alter table allu.location add column new_payment_tariff_override text;
update allu.location set new_payment_tariff=payment_tariff::text;
update allu.location set new_payment_tariff_override=payment_tariff_override::text;
alter table allu.location drop column payment_tariff;
alter table allu.location drop column payment_tariff_override;
alter table allu.location rename column new_payment_tariff to payment_tariff;
alter table allu.location rename column new_payment_tariff_override to payment_tariff_override;
