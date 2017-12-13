-- Helper function: finds area id by name. If name is not found, Throws error.
CREATE OR REPLACE FUNCTION get_area_id(areaname TEXT)
 RETURNS INT AS $$
DECLARE
 area_id INT;
BEGIN
 SELECT id INTO area_id FROM allu.location_area WHERE name = areaname;
 IF NOT FOUND THEN
  RAISE no_data_found;
 END IF;
 RETURN area_id;
END;
$$ LANGUAGE plpgsql;

-- Short term rental - dog training event (uses same areas as dog training fields)

insert into allu.fixed_location values (DEFAULT, get_area_id('Heikinlaakson kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kivikon kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Koneen kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Kontulan kelkkapuisto'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Malminkartanon sirkuskenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Munkkipuiston kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Pajalahden kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Pyhtään puiston kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Sahaajankadun kenttä, hiekka-alue'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Sahaajankadun kenttä, nurmialue'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Skatan kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Talinhuipun kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Tattarisuon täyttömäen kenttä'), NULL, 'DOG_TRAINING_EVENT', true);
insert into allu.fixed_location values (DEFAULT, get_area_id('Viilarintien kenttä'), NULL, 'DOG_TRAINING_EVENT', true);

update allu.fixed_location as flTo set (geometry) =
(select geometry
 from allu.fixed_location flFrom
 where flFrom.application_kind = 'DOG_TRAINING_FIELD'
  and flTo.area_id = flFrom.area_id)
where flTo.application_kind = 'DOG_TRAINING_EVENT';

DROP FUNCTION get_area_id(TEXT);