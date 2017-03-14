-- Insert pricing data (from http://www.hel.fi/static/hkr/luvat/maksut_tapahtumat.pdf):

-- Helper function: finds area id by name. If name is not found, creates
-- a new area with the given name and returns its id.
CREATE FUNCTION get_area_id(areaname TEXT)
RETURNS INT AS $$
DECLARE
   area_id INT;
BEGIN
   SELECT id INTO area_id FROM allu.location_area WHERE name = areaname;
   IF NOT FOUND THEN
       INSERT INTO allu.location_area (name) values (areaname) RETURNING id INTO area_id;
   END IF;
   RETURN area_id;
END;
$$ LANGUAGE plpgsql;

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'C', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'D', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 12000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 18000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Mauno Koiviston aukio'), 'E', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'C', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'D', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'E', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 3000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 9000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'F', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 3000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 6000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 9000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 7000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 14000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 21000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'C', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 4000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 8000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 12000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'C', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'D', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 10000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 15000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 20000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 40000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 60000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 15000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 45000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'C', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 15000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 45000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), NULL, 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 20000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 40000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 60000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Senaatintori'), NULL, 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 30000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 60000000, 50, 50, 14, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 90000000, 0, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Säiliö 468'), NULL, 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 2000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Asema-aukio'), 'A', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, '{500000, 250000}', '{100, 300}', NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 5000000, 50, 50, 14, NULL, NULL, '{10000, 5000, 2500}', '{0, 2000, 4000}'),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 5000000, 0, 0, 0, NULL, NULL, '{20000}', '{0}');

insert into allu.fixed_location values (DEFAULT, get_area_id('Asema-aukio'), 'B', 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, '{500000, 250000}', '{100, 300}', NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 5000000, 50, 50, 14, NULL, NULL, '{10000, 5000, 2500}', '{0, 2000, 4000}'),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 5000000, 0, 0, 0, NULL, NULL, '{20000}', '{0}');

insert into allu.fixed_location values (DEFAULT, get_area_id('Kolmensepänaukio'), NULL, 'OUTDOOREVENT', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_FREE', 5000000, 50, 50, 14, '{500000, 250000}', '{100, 300}', NULL, NULL),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PUBLIC_NONFREE', 5000000, 50, 50, 14, NULL, NULL, '{10000, 5000, 2500}', '{0, 2000, 4000}'),
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'CLOSED', 5000000, 0, 0, 0, NULL, NULL, '{20000}', '{0}');

-- Promotions

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 6000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 6000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'C', 'PROMOTION', true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 6000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'),'D', 'PROMOTION', true);
insert into allu.outdoor_pricing  values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 6000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Mauno Koiviston aukio'), 'E', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'C', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'D', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'E', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Rautatientori'), 'F', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kansalaistori'), 'C', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'C', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaisaniemenpuisto'), 'D', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Töölönlahden puisto'), 'C', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), NULL, 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Senaatintori'), NULL, 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Säiliö 468'), NULL, 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 1250000, 50, 0, 0, '{125000}', '{10}', NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Asema-aukio'), 'A', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Asema-aukio'), 'B', 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, NULL, NULL, NULL, NULL);

insert into allu.fixed_location values (DEFAULT, get_area_id('Kolmensepänaukio'), NULL, 'PROMOTION', true);
insert into allu.outdoor_pricing values
  (DEFAULT, currval(pg_get_serial_sequence('allu.fixed_location', 'id')), NULL, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL);

-- Short term rental - banderols

insert into allu.fixed_location values (DEFAULT, get_area_id('1a Sörnäisten rantatie – Kaikukatu, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('1b Sörnäisten rantatie – Kaikukatu, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('2a Sörnäisten rantatie – Vilhonvuorenkatu, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('2b Sörnäisten rantatie – Vilhonvuorenkatu,Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('4a Itäväylä – Lautturinkuja, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('4b Itäväylä – Lautturinkuja, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('5b Sörnäisten rantatie – Näkinsilta, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('10a Porkkalankatu – Mechelininkatu, Satamaan päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('10b Porkkalankatu – Mechelininkatu, Töölöön päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('11a Mäkelänkatu – Asemapäällikönkatu, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('11b Mäkelänkatu – Asemapäällikönkatu, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('12a Huopalahdentie - Kevyen liikenteen silta (Munkkiniemi), Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('14a Turunlinnantie – Kevyen liikenteen väylä (Stoa), Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('14b Turunlinnantie – Kevyen liikenteen väylä (Stoa), Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('15a Pitäjänmäentie – Ylityspolku, Länteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('17a Konalantie – Pitäjänmäenpolku, Konalaan päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('18a Rautatieläisenkatu – Junailijankuja, Itään/Mäkelänkadulle päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('18b Rautatieläisenkatu – Junailijankuja, Länteen/Pasilan asemalle päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('19a Helsinginkatu – Wallininkatu, Kallioon päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('19b Helsinginkatu – Wallininkatu, Töölöön päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('30a Teollisuuskatu – Sturenkatu, Itään päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('30b Teollisuuskatu – Sturenkatu, Länteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('31a Kontulantie - Kevyen liikenteen väylä, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('31b Kontulantie - Kevyen liikenteen väylä, Kontulaan päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('32a Laajasalontie – Kuukiventie, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('32b Laajasalontie – Kuukiventie, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('33a Vuotie – Mosaiikkitori (Kevyen liikenteen silta), Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('33b Vuotie – Mosaiikkitori (Kevyen liikenteen silta), Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('34a Vuotie – Kallvikintie, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('34b Vuotie – Kallvikintie, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('35a Vuotie – Rastilan leirintäalue (Karavaanisilta), Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('35b Vuotie – Rastilan leirintäalue (Karavaanisilta), Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('36a Klaneettitie – Sitratori (Kevyen liikenteen silta), Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('36b Klaneettitie – Sitratori (Kevyen liikenteen silta), Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('37a Laajasalontie – Kirkkosalmentie, Kaupunkiin päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('37b Laajasalontie – Kirkkosalmentie, Kaupungista poispäin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('38a Baana – Mannerheimintie, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('38b Baana – Mannerheimintie, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('39a Baana – Arkadiankatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('39b Baana – Arkadiankatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('40a Baana – Antinkatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('40b Baana – Antinkatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('41a Baana – Jaakonkatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('41b Baana – Jaakonkatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('42a Baana – Fredrikinkatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('42b Baana – Fredrikinkatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('43a Baana – Runeberginkatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('43b Baana – Runeberginkatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('44a Baana – Lapinlahdenkatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('44b Baana – Lapinlahdenkatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('45a Baana – Porkkalankatu, Ruoholahteen päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('45b Baana – Porkkalankatu, Kansalaistorille päin ajettaessa'), NULL, 'BRIDGE_BANNER', true);

-- Short term rental - dog training fields

insert into allu.fixed_location values (DEFAULT, get_area_id('Heikinlaakson kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kivikon kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Koneen kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kontulan kelkkapuisto'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Malminkartanon sirkuskenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Munkkipuiston kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Pajalahden kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Pyhtään puiston kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Sahaajankadun kenttä, hiekka-alue'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Sahaajankadun kenttä, nurmialue'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Skatan kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Talinhuipun kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Tattarisuon täyttömäen kenttä'), NULL, 'DOG_TRAINING_FIELD', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Viilarintien kenttä'), NULL, 'DOG_TRAINING_FIELD', true);

-- Short term rental - seasonal sales locations

insert into allu.fixed_location values (DEFAULT, get_area_id('Heikkilänaukio'), NULL, 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'A', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'B', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'C', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'D', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'E', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'F', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'G', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'H', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'I', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Hesperianpuisto'), 'J', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '1', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '2', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '3', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '4', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '5', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kaivopuisto'), '6', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'A', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'B', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'C', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'D', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'E', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Keskuskatu'), 'F', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kolmensepänaukio'), 'Muu', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kolmensepänaukio'), 'Joulu', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A1', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A2', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A3', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A4', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A5', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A6', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A7', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A8', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'A9', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B1', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B2', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B3', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B4', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B5', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B6', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B7', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B8', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Narinkka'), 'B9', 'SEASON_SALE', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Ruoholahdentori'), NULL, 'SEASON_SALE', true);

-- Zone pricing:

-- NOTE: event pricing for locations "Asema-aukio" and "Kolmensepänaukio" is
-- the same as for Zone 1, so if zone pricing changes those locations need
-- to be updated too!

-- Public free events have extra fees for structures:
insert into allu.outdoor_pricing values
  (DEFAULT, NULL, 1, 'PUBLIC_FREE', 5000000, 50, 50, 14, '{500000, 250000}', '{100, 300}', NULL, NULL),
  (DEFAULT, NULL, 2, 'PUBLIC_FREE', 2500000, 50, 50, 14, '{250000, 125000}', '{100, 300}', NULL, NULL),
  (DEFAULT, NULL, 3, 'PUBLIC_FREE', 1250000, 50, 50, 14, '{125000, 62500}', '{100, 300}', NULL, NULL);

-- Public nonfree events have extra fee for area, not structure:
insert into allu.outdoor_pricing values
  (DEFAULT, NULL, 1, 'PUBLIC_NONFREE', 5000000, 50, 50, 14, NULL, NULL, '{10000, 5000, 2500}', '{0, 2000, 4000}'),
  (DEFAULT, NULL, 2, 'PUBLIC_NONFREE', 2500000, 50, 50, 14, NULL, NULL, '{5000, 2500, 1250}', '{0, 2000, 4000}'),
  (DEFAULT, NULL, 3, 'PUBLIC_NONFREE', 1250000, 50, 50, 14, NULL, NULL, '{2500, 1250, 625}', '{0, 2000, 4000}');

-- Closed events have extra fee for area and no discounts:
insert into allu.outdoor_pricing values
  (DEFAULT, NULL, 1, 'CLOSED', 5000000, 0, 0, 0, NULL, NULL, '{20000}', '{0}'),
  (DEFAULT, NULL, 2, 'CLOSED', 2500000, 0, 0, 0, NULL, NULL, '{20000}', '{0}'),
  (DEFAULT, NULL, 3, 'CLOSED', 1250000, 0, 0, 0, NULL, NULL, '{20000}', '{0}');

  -- Promotion events have extra fees for structures and no duration discount:
insert into allu.outdoor_pricing values
  (DEFAULT, NULL, 1, 'PROMOTION', 5000000, 50, 0, 0, '{500000}', '{10}', NULL, NULL),
  (DEFAULT, NULL, 2, 'PROMOTION', 2500000, 50, 0, 0, '{250000}', '{10}', NULL, NULL),
  (DEFAULT, NULL, 3, 'PROMOTION', 1250000, 50, 0, 0, '{125000}', '{10}', NULL, NULL);

-- no need for the helper function anymore
DROP FUNCTION get_area_id(TEXT)