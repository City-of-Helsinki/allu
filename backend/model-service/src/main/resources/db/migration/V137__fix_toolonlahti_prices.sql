delete from allu.outdoor_pricing where fixed_location_id is null and zone_id is null;
CREATE FUNCTION add_fixed_location_price(areaname TEXT, sectionname TEXT, kind TEXT, nature TEXT, base_charge INTEGER)
RETURNS VOID AS $$
DECLARE
  fixed_location_id INT;
BEGIN
  IF sectionname IS NULL OR sectionname = '' THEN
      SELECT f.id INTO fixed_location_id from allu.fixed_location f, allu.location_area a where a.name = areaname and f.application_kind = kind and a.id = f.area_id and f.is_active = true;      
  ELSE
      SELECT f.id INTO fixed_location_id from allu.fixed_location f, allu.location_area a where a.name = areaname and f.section = sectionname and f.application_kind = kind and a.id = f.area_id and f.is_active = true;      
  END IF;
  INSERT INTO allu.outdoor_pricing (fixed_location_id, nature, base_charge, build_discount_percent)
  VALUES (fixed_location_id, nature, base_charge, 50); 
END;
$$ LANGUAGE plpgsql;

DO $$ BEGIN
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'A', 'PROMOTION', 'PROMOTION', 20000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 40000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'A', 'OUTDOOREVENT', 'CLOSED', 40000000);

    PERFORM add_fixed_location_price('Töölönlahden puisto', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 10000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'B', 'PROMOTION', 'PROMOTION', 10000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'B', 'OUTDOOREVENT', 'CLOSED', 20000000);

    PERFORM add_fixed_location_price('Töölönlahden puisto', 'C', 'OUTDOOREVENT', 'PUBLIC_FREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'C', 'PROMOTION', 'PROMOTION', 20000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'C', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 40000000);
    PERFORM add_fixed_location_price('Töölönlahden puisto', 'C', 'OUTDOOREVENT', 'CLOSED', 40000000);
END $$ LANGUAGE plpgsql;    
DROP FUNCTION add_fixed_location_price(TEXT, TEXT, TEXT, TEXT, INTEGER);

