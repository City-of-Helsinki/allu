-- DO NOT EDIT THIS FILE MANUALLY!

-- This file was autogenerated. The command used was:
-- java -Dloader.main=fi.hel.allu.model.deployment.LocationGeometryReader -classpath ./model-service-1.0-SNAPSHOT.jar org.springframework.boot.loader.PropertiesLauncher kolme_seppaa_paivetetty.geojson kol,e.sql

-- See the documentation for more info.

CREATE FUNCTION check_allu_name(areaname TEXT, sectionname TEXT)
RETURNS VOID AS $$
DECLARE
   match_count INT;
BEGIN
   IF sectionname IS NULL OR sectionname = '' THEN
       SELECT COUNT(*) INTO STRICT match_count FROM
           (SELECT DISTINCT a.name as areaname, fl.section as section FROM
            allu.fixed_location as fl inner join allu.location_area as a on fl.area_id=a.id) as l WHERE
           l.areaname LIKE check_allu_name.areaname AND l.section IS NULL;
   ELSE
       SELECT COUNT(*) INTO STRICT match_count FROM
           (SELECT DISTINCT a.name as areaname, fl.section as section FROM
            allu.fixed_location as fl inner join allu.location_area as a on fl.area_id=a.id) as l WHERE
           l.areaname LIKE check_allu_name.areaname AND l.section = sectionname;
   END IF;
   IF match_count <> 1 THEN
       RAISE EXCEPTION 'Name % matches % areas in database', check_allu_name.areaname, match_count;
   END IF;
END;
$$ LANGUAGE plpgsql;
-- input file: kolme_seppaa_paivetetty.geojson
select check_allu_name('Kolmensepänaukio%',null);
update allu.fixed_location
set geometry = 'SRID=3879;GEOMETRYCOLLECTION(MULTIPOLYGON(((25496713.829496324 6672869.447513749,25496708.76945399 6672876.26323576,25496713.580543123 6672879.835036821,25496718.640585054 6672873.019314887,25496713.829496324 6672869.447513749))))'
where fixed_location.area_id = (select location_area.id
from allu.location_area location_area
where location_area.name like 'Kolmensepänaukio%') and fixed_location.section is null;
DROP FUNCTION check_allu_name(TEXT, TEXT);
