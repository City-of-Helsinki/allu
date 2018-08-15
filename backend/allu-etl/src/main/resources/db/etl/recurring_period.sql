INSERT INTO allureport.toistuvuusjakso (
  id,
  hakemus_id,
  toistuvuus_alku,
  toistuvuus_loppu
)
SELECT
    r.id AS id,
    r.application_id AS hakemus_id,
    r.period_start_time AS toistuvuus_alku,
    r.period_end_time AS toistuvuus_loppu
FROM allu_operative.recurring_period r
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    toistuvuus_alku = EXCLUDED.toistuvuus_alku,
    toistuvuus_loppu = EXCLUDED.toistuvuus_loppu
;
