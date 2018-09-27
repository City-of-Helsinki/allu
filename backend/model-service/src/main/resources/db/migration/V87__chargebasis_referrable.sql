alter table allu.charge_basis add column referrable boolean;
update allu.charge_basis set referrable = (tag <> null);
update allu.charge_basis set tag = 'ECO' where text = 'Ekokompassi-alennus -30%';


