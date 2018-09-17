alter table allu.invoice_row add column charge_basis_id integer references allu.charge_basis(id);
