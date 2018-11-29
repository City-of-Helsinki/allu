alter table allu.application add column invoicing_period_length integer;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'invoicingPeriodLength', 'Laskutusjakson pituus kuukausissa', 'INTEGER');