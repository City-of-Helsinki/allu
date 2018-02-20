alter table allu.customer add column invoicing_operator text;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Customer'), 'invoicingOperator', 'Verkkolaskuoperaattorin tunnus', 'STRING');