alter table allu.application add column skip_price_calculation boolean;

update allu.application set skip_price_calculation=false;

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Application'), 'skipPriceCalculation', 'Ohita automaattinen hinnanlaskenta', 'BOOLEAN');
