INSERT INTO allureport.alue (
  id,
  nimi
)
SELECT
    a.id AS id,
    a.name AS nimi
FROM allu_operative.location_area a
ON CONFLICT (id) DO UPDATE SET
    nimi = EXCLUDED.nimi
;

DELETE FROM allureport.alue a WHERE NOT EXISTS (SELECT id FROM allu_operative.location_area ol WHERE ol.id = a.id);
