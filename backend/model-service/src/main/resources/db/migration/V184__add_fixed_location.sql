--Funciton to create missing or new location_area
CREATE OR REPLACE FUNCTION get_area_id(areaname TEXT)
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
--Insert new fixed locations
insert into allu.fixed_location values (DEFAULT, get_area_id('Uutelan koirakoulutuskenttä'), NULL, 'DOG_TRAINING_FIELD', true, 'SRID=3879;GEOMETRYCOLLECTION(POLYGON((25509960.604096677 6676530.813961102,25509981.855259318 6676551.930622715,25509950.6510205 6676581.655350208,25509916.756761096 6676548.299094921,25509925.364826977 6676542.919053745,25509960.604096677 6676530.813961102)))');
insert into allu.fixed_location values (DEFAULT, get_area_id('Uutelan koirakoulutuskenttä'), NULL, 'OUTDOOREVENT', true, 'SRID=3879;GEOMETRYCOLLECTION(POLYGON((25509960.604096677 6676530.813961102,25509981.855259318 6676551.930622715,25509950.6510205 6676581.655350208,25509916.756761096 6676548.299094921,25509925.364826977 6676542.919053745,25509960.604096677 6676530.813961102)))');