INSERT INTO allureport.sijainti_geometria (
  id,
  geometria,
  sijainti_id
)
SELECT
    g.id AS id,
    g.geometry as geometria,
    g.location_id AS sijainti_id
FROM allu_operative.location_geometry g
ON CONFLICT (id) DO UPDATE SET
    geometria = EXCLUDED.geometria,
    sijainti_id = EXCLUDED.sijainti_id
;

DELETE FROM allureport.sijainti_geometria s WHERE NOT EXISTS (SELECT id FROM allu_operative.location_geometry ol WHERE ol.id = s.id);
