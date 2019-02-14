delete from allu.outdoor_pricing;

alter table allu.outdoor_pricing
  drop column duration_discount_limit,
  drop column duration_discount_percent,
  drop column structure_extra_charges,
  drop column structure_extra_charge_limits,
  drop column area_extra_charges,
  drop column area_extra_charge_limits
;

alter table allu.outdoor_pricing
  add column surface_hardness text
;

-- Add event prices for fixed locations

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
    PERFORM add_fixed_location_price('Narinkka', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'A', 'PROMOTION', 'PROMOTION', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 12000000);
    PERFORM add_fixed_location_price('Narinkka', 'A', 'OUTDOOREVENT', 'CLOSED', 12000000);
    
    PERFORM add_fixed_location_price('Narinkka', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'B', 'PROMOTION', 'PROMOTION', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 12000000);
    PERFORM add_fixed_location_price('Narinkka', 'B', 'OUTDOOREVENT', 'CLOSED', 12000000);

    PERFORM add_fixed_location_price('Narinkka', 'C', 'OUTDOOREVENT', 'PUBLIC_FREE', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'C', 'PROMOTION', 'PROMOTION', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'C', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 12000000);
    PERFORM add_fixed_location_price('Narinkka', 'C', 'OUTDOOREVENT', 'CLOSED', 12000000);
    
    PERFORM add_fixed_location_price('Narinkka', 'D', 'OUTDOOREVENT', 'PUBLIC_FREE', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'D', 'PROMOTION', 'PROMOTION', 6000000);
    PERFORM add_fixed_location_price('Narinkka', 'D', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 12000000);
    PERFORM add_fixed_location_price('Narinkka', 'D', 'OUTDOOREVENT', 'CLOSED', 12000000);
    
    PERFORM add_fixed_location_price('Mauno Koiviston aukio', NULL, 'OUTDOOREVENT', 'PUBLIC_FREE', 4000000);
    PERFORM add_fixed_location_price('Mauno Koiviston aukio', NULL, 'PROMOTION', 'PROMOTION', 4000000);
    PERFORM add_fixed_location_price('Mauno Koiviston aukio', NULL, 'OUTDOOREVENT', 'PUBLIC_NONFREE', 8000000);
    PERFORM add_fixed_location_price('Mauno Koiviston aukio', NULL, 'OUTDOOREVENT', 'CLOSED', 8000000);
    
    PERFORM add_fixed_location_price('Rautatientori', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'A', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Rautatientori', 'A', 'OUTDOOREVENT', 'CLOSED', 10000000);
    
    PERFORM add_fixed_location_price('Rautatientori', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'B', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Rautatientori', 'B', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Rautatientori', 'C', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'C', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'C', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Rautatientori', 'C', 'OUTDOOREVENT', 'CLOSED', 10000000);
    
    PERFORM add_fixed_location_price('Rautatientori', 'D', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'D', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Rautatientori', 'D', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Rautatientori', 'D', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Rautatientori', 'E', 'OUTDOOREVENT', 'PUBLIC_FREE', 2000000);
    PERFORM add_fixed_location_price('Rautatientori', 'E', 'PROMOTION', 'PROMOTION', 2000000);
    PERFORM add_fixed_location_price('Rautatientori', 'E', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 4000000);
    PERFORM add_fixed_location_price('Rautatientori', 'E', 'OUTDOOREVENT', 'CLOSED', 4000000);

    PERFORM add_fixed_location_price('Rautatientori', 'F', 'OUTDOOREVENT', 'PUBLIC_FREE', 2000000);
    PERFORM add_fixed_location_price('Rautatientori', 'F', 'PROMOTION', 'PROMOTION', 2000000);
    PERFORM add_fixed_location_price('Rautatientori', 'F', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 4000000);
    PERFORM add_fixed_location_price('Rautatientori', 'F', 'OUTDOOREVENT', 'CLOSED', 4000000);
    
    PERFORM add_fixed_location_price('Kansalaistori', NULL, 'OUTDOOREVENT', 'PUBLIC_FREE', 6000000);
    PERFORM add_fixed_location_price('Kansalaistori', NULL, 'PROMOTION', 'PROMOTION', 6000000);
    PERFORM add_fixed_location_price('Kansalaistori', NULL, 'OUTDOOREVENT', 'PUBLIC_NONFREE', 12000000);
    PERFORM add_fixed_location_price('Kansalaistori', NULL, 'OUTDOOREVENT', 'CLOSED', 12000000);
    
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'A', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'A', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'B', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'B', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'C', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'C', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'C', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'C', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'D', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'D', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'D', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Kaisaniemenpuisto', 'D', 'OUTDOOREVENT', 'CLOSED', 10000000);
    
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'A', 'PROMOTION', 'PROMOTION', 20000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 40000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'A', 'OUTDOOREVENT', 'CLOSED', 40000000);

    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 10000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'B', 'PROMOTION', 'PROMOTION', 10000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'B', 'OUTDOOREVENT', 'CLOSED', 20000000);

    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'C', 'OUTDOOREVENT', 'PUBLIC_FREE', 20000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'C', 'PROMOTION', 'PROMOTION', 20000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'C', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 40000000);
    PERFORM add_fixed_location_price('Töölönlahdenpuisto', 'C', 'OUTDOOREVENT', 'CLOSED', 40000000);

    PERFORM add_fixed_location_price('Senaatintori', NULL, 'OUTDOOREVENT', 'PUBLIC_FREE', 30000000);
    PERFORM add_fixed_location_price('Senaatintori', NULL, 'PROMOTION', 'PROMOTION', 30000000);
    PERFORM add_fixed_location_price('Senaatintori', NULL, 'OUTDOOREVENT', 'PUBLIC_NONFREE', 60000000);
    PERFORM add_fixed_location_price('Senaatintori', NULL, 'OUTDOOREVENT', 'CLOSED', 60000000);

    PERFORM add_fixed_location_price('Säiliö 468', NULL, 'OUTDOOREVENT', 'PUBLIC_FREE', 2000000);
    PERFORM add_fixed_location_price('Säiliö 468', NULL, 'PROMOTION', 'PROMOTION', 2000000);
    PERFORM add_fixed_location_price('Säiliö 468', NULL, 'OUTDOOREVENT', 'PUBLIC_NONFREE', 2000000);
    PERFORM add_fixed_location_price('Säiliö 468', NULL, 'OUTDOOREVENT', 'CLOSED', 2000000);

    PERFORM add_fixed_location_price('Kolmensepänaukio', NULL, 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Kolmensepänaukio', NULL, 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Kolmensepänaukio', NULL, 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Kolmensepänaukio', NULL, 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Asema-aukio', 'A', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'A', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'A', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'A', 'OUTDOOREVENT', 'CLOSED', 10000000);

    PERFORM add_fixed_location_price('Asema-aukio', 'B', 'OUTDOOREVENT', 'PUBLIC_FREE', 5000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'B', 'PROMOTION', 'PROMOTION', 5000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'B', 'OUTDOOREVENT', 'PUBLIC_NONFREE', 10000000);
    PERFORM add_fixed_location_price('Asema-aukio', 'B', 'OUTDOOREVENT', 'CLOSED', 10000000);

END $$ LANGUAGE plpgsql;    

DROP FUNCTION add_fixed_location_price(TEXT, TEXT, TEXT, TEXT, INTEGER);

-- Add event prices for zones

-- Public free events and promotions
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PUBLIC_FREE', 5000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PUBLIC_FREE', 10000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PROMOTION', 5000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PROMOTION', 10000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PUBLIC_FREE', 2500000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PUBLIC_FREE', 5000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PROMOTION', 2500000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PROMOTION', 5000000, 50, 'SOFT');

INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PUBLIC_FREE', 1250000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PUBLIC_FREE', 2500000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PROMOTION', 1250000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PROMOTION', 2500000, 50, 'SOFT');
  
-- Public non free and closed events
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PUBLIC_NONFREE', 10000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'PUBLIC_NONFREE', 20000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'CLOSED', 10000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'CLOSED', 20000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PUBLIC_NONFREE', 5000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'PUBLIC_NONFREE', 10000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'CLOSED', 5000000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'CLOSED', 10000000, 50, 'SOFT');

INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PUBLIC_NONFREE', 2500000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'PUBLIC_NONFREE', 5000000, 50, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'CLOSED', 2500000, 50, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'CLOSED', 5000000, 50, 'SOFT'); 
  
-- Big events
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'BIG_EVENT', 5000000, 0, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (1, 'BIG_EVENT', 10000000, 0, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'BIG_EVENT', 2500000, 0, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (2, 'BIG_EVENT', 5000000, 0, 'SOFT');
  
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'BIG_EVENT', 2500000, 0, 'HARD');
INSERT INTO allu.outdoor_pricing (zone_id, nature, base_charge, build_discount_percent, surface_hardness) 
  VALUES (3, 'BIG_EVENT', 5000000, 0, 'SOFT');
