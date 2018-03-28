INSERT INTO allureport.kaupunginosa
SELECT
    c.id AS id,
    c.district_id AS tunnus,
    c.name AS nimi
FROM allu_operative.city_district c
ON CONFLICT (id) DO UPDATE SET
    tunnus = EXCLUDED.tunnus,
    nimi = EXCLUDED.nimi
;

