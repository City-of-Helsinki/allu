INSERT INTO allureport.liite
SELECT
    aa.id AS id,
    aa.application_id AS hakemus_id,
    CASE
        WHEN a.type = 'ADDED_BY_CUSTOMER' THEN 'Asiakkaan lisäämä liite'
        WHEN a.type = 'ADDED_BY_HANDLER' THEN 'Käsittelijän lisäämä liite'
        WHEN a.type = 'DEFAULT' THEN 'Hakemustyyppikohtainen vakioliite'
        WHEN a.type = 'DEFAULT_IMAGE' THEN 'Hakemustyyppikohtainen tyyppikuvaliite'
        WHEN a.type = 'DEFAULT_TERMS' THEN 'Hakemustyyppikohtainen ehtoliite'
    END AS tyyppi,
    a.name AS nimi,
    a.description AS kuvaus,
    a.decision_attachment AS paatoksen_liite,
    d.size AS koko,
    a.creation_time AS luonti_aika,
    a.mime_type AS mime_tyyppi,
    d.data AS data
FROM allu_operative.application_attachment aa
LEFT JOIN allu_operative.attachment a ON aa.attachment_id = a.id
LEFT JOIN allu_operative.attachment_data d ON a.attachment_data_id = a.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    tyyppi = EXCLUDED.tyyppi,
    nimi = EXCLUDED.nimi,
    kuvaus = EXCLUDED.kuvaus,
    paatoksen_liite = EXCLUDED.paatoksen_liite,
    koko = EXCLUDED.koko,
    luonti_aika = EXCLUDED.luonti_aika,
    mime_tyyppi = EXCLUDED.mime_tyyppi,
    data = EXCLUDED.data;