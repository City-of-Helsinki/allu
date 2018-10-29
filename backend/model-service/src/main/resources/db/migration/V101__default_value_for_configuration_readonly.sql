alter table allu.configuration alter column readonly set default false;
update allu.configuration set readonly=true where key='PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR';

CREATE OR REPLACE FUNCTION allu.get_placement_contract_section_number()
RETURNS INT AS $$
DECLARE
  year TEXT;
  cur_year TEXT;
  conf_count INT;
BEGIN
  select date_part('year',(now())) into cur_year;
  select value into year from allu.configuration where key='PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR';
  select count(*) into conf_count from allu.configuration where key='PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR';
  if (year is null or year <> cur_year) then
    alter sequence allu.placement_contract_section_number restart with 1;
    if (conf_count < 1) then
      insert into allu.configuration (key, type, value, readonly) values ('PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR', 'TEXT', cur_year, true);
    else
      update allu.configuration set value=cur_year where key='PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR';
    end if;
  end if;
  return nextval('allu.placement_contract_section_number');
END;
$$ LANGUAGE plpgsql;
