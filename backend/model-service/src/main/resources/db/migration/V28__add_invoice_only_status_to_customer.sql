alter table allu.customer add column invoicing_only boolean;
update allu.customer set invoicing_only=false;
alter table allu.customer alter column invoicing_only set not null;

insert into allu.attribute_meta (structure_meta_id, name, ui_name,data_type) values
  ((select id from allu.structure_meta where type_name='Customer'), 'invoicingOnly', 'Laskutusasiakas', 'BOOLEAN');
