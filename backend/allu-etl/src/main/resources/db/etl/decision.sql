INSERT INTO allureport.paatos
SELECT
    d.id AS id,
    d.application_id AS hakemus_id,
    d.creation_time AS luonti_aika,
    d.data AS data
FROM allu_operative.decision d
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    luonti_aika = EXCLUDED.luonti_aika,
    data = EXCLUDED.data
;