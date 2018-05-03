create sequence allu.placement_contract_section_number;

CREATE FUNCTION allu.get_placement_contract_section_number()
RETURNS INT AS $$
DECLARE
  year TEXT;
  cur_year TEXT;
  conf_count INT;
BEGIN
  select date_part('year',(now())) into cur_year;
  select value into year from allu.configuration where type='placement_contract_section_number_year';
  select count(*) into conf_count from allu.configuration where type='placement_contract_section_number_year';
  if (year is null or year <> cur_year) then
    alter sequence allu.placement_contract_section_number restart with 1;
    if (conf_count < 1) then
      insert into allu.configuration (type, value) values ('placement_contract_section_number_year', cur_year);
    else
      update allu.configuration set value=cur_year where type='placement_contract_section_number_year';
    end if;
  end if;
  return nextval('allu.placement_contract_section_number');
END;
$$ LANGUAGE plpgsql;

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values
  ((select id from allu.structure_meta where type_name='PLACEMENT_CONTRACT'),'sectionNumber','Pykälänumero','INTEGER');
