INSERT INTO allureport.hakemus_asiakas
SELECT
    a.id AS id,
    a.customer_id AS asiakas_id,
    a.application_id AS hakemus_id,
    CASE
        WHEN a.customer_role_type = 'APPLICANT' THEN 'Hakija'
        WHEN a.customer_role_type = 'PROPERTY_DEVELOPER' THEN 'Rakennuttaja'
        WHEN a.customer_role_type = 'CONTRACTOR' THEN 'Urakoitsija'
        WHEN a.customer_role_type = 'REPRESENTATIVE' THEN 'Asiamies'
    END AS asiakkaan_rooli
FROM allu_operative.application_customer a
ON CONFLICT (id) DO UPDATE SET
    asiakas_id = EXCLUDED.asiakas_id,
    hakemus_id = EXCLUDED.hakemus_id,
    asiakkaan_rooli = EXCLUDED.asiakkaan_rooli
;
