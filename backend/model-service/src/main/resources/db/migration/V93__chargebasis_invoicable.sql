alter table allu.charge_basis add column invoicable boolean;
update allu.charge_basis set invoicable = true;
