INSERT INTO allureport.kommentti (
  id,
  hakemus_id,
  lisaaja,
  tyyppi,
  teksti,
  luontiaika,
  paivitysaika,
  kommentoija,
  hanke_id
)
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
        WHEN type = 'PROPOSE_TERMINATION' THEN 'Ehdotettu irtisanottavaksi'
        WHEN type = 'EXTERNAL_SYSTEM' THEN 'Asiakasjärjestelmän kommentti'
    END AS tyyppi,
    c.text AS teksti,
    c.create_time AS luontiaika,
    c.update_time AS paivitysaika,
    c.commentator AS kommentoija,
    c.project_id AS hanke_id
FROM allu_operative.comment c
LEFT JOIN allu_operative.user u ON c.user_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    lisaaja = EXCLUDED.lisaaja,
    tyyppi = EXCLUDED.tyyppi,
    teksti = EXCLUDED.teksti,
    luontiaika = EXCLUDED.luontiaika,
    paivitysaika = EXCLUDED.paivitysaika,
    hanke_id = EXCLUDED.hanke_id,
    kommentoija = EXCLUDED.kommentoija
;

