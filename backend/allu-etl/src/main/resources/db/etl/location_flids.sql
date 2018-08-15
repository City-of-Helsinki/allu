INSERT INTO allureport.sijainti_kiinteasijainti  (
  id,
  sijainti_id,
  kiinteasijainti_id
)
SELECT
    l.id AS id,
    l.location_id AS sijainti_id,
    l.fixed_location_id AS kiinteasijainti_id
FROM allu_operative.location_flids l
ON CONFLICT (id) DO UPDATE SET
    sijainti_id = EXCLUDED.sijainti_id,
    kiinteasijainti_id = EXCLUDED.kiinteasijainti_id
;

