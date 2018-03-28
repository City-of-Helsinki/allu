INSERT INTO allureport.hakemuskommentti
SELECT
    c.id AS id,
    c.application_id AS hakemus_id,
    u.user_name AS lisaaja,
    CASE
        WHEN type = 'INTERNAL' THEN 'Sisäinen kommentti'
        WHEN type = 'INVOICING' THEN 'Laskutuksen kommentti'
        WHEN type = 'RETURN' THEN 'Valmisteluun palauttajan kommentti'
        WHEN type = 'REJECT' THEN 'Hylkääjän kommentti'
        WHEN type = 'PROPOSE_APPROVAL' THEN 'Ehdotettu hyväksyttäväksi'
        WHEN type = 'PROPOSE_REJECT' THEN 'Ehdotettu hylättäväksi'
    END AS tyyppi,
    c.text AS teksti,
    c.create_time AS luonti_aika,
    c.update_time AS paivitys_aika
FROM allu_operative.application_comment c
LEFT JOIN allu_operative.user u ON c.user_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    lisaaja = EXCLUDED.lisaaja,
    tyyppi = EXCLUDED.tyyppi,
    teksti = EXCLUDED.teksti,
    luonti_aika = EXCLUDED.luonti_aika,
    paivitys_aika = EXCLUDED.paivitys_aika
;