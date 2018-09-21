-- Add field indicating whether invoicing has changed after last decision
alter table allu.application add column invoicing_changed boolean;
update allu.application set invoicing_changed = false;
alter table allu.application alter column invoicing_changed set not null;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='Application'), 'invoicingChanged', 'Laskutustietoja päivitetty', 'BOOLEAN');
