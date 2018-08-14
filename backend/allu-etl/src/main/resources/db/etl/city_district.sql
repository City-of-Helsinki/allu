INSERT INTO allureport.kaupunginosa (
  id,
  tunnus,
  nimi
)
SELECT
    c.id AS id,
    c.district_id AS tunnus,
    c.name AS nimi
FROM allu_operative.city_district c
ON CONFLICT (id) DO UPDATE SET
    tunnus = EXCLUDED.tunnus,
    nimi = EXCLUDED.nimi
;

DELETE FROM allureport.kaupunginosa k WHERE NOT EXISTS (SELECT id FROM allu_operative.city_district oc WHERE oc.id = k.id);
