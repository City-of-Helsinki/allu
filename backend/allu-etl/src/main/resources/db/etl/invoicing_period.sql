INSERT INTO allureport.laskutusjakso (
  id,
  hakemus_id,
  suljettu,
  alkuaika,
  loppuaika
)
SELECT
    i.id AS id,
    i.application_id AS hakemus_id,
    i.closed AS suljettu,
    i.start_time AS alkuaika,
    i.end_time AS loppuaika
FROM allu_operative.invoicing_period i
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    suljettu = EXCLUDED.suljettu,
    alkuaika = EXCLUDED.alkuaika,
    loppuaika = EXCLUDED.loppuaika
;

