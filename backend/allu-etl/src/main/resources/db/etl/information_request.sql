INSERT INTO allureport.taydennyspyynto (
  id,
  hakemus_id,
  luontiaika,
  lisaaja,
  status
)
SELECT
    i.id AS id,
    i.application_id AS hakemus_id,
    i.creation_time AS luontiaika,
    u.user_name AS lisaaja,
    CASE
        WHEN i.status = 'OPEN' THEN 'Avoin'
        WHEN i.status = 'RESPONSE_RECEIVED' THEN 'Vastattu'
        WHEN i.status = 'CLOSED' THEN 'Suljettu'
    END AS status
FROM allu_operative.information_request i
LEFT JOIN allu_operative.user u on i.creator_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    luontiaika = EXCLUDED.luontiaika,
    lisaaja = EXCLUDED.lisaaja,
    status = EXCLUDED.status
;

INSERT INTO allureport.taydennyspyynto_kentta (
  id,
  taydennyspyynto_id,
  kentta,
  kuvaus
)
SELECT
    i.id AS id,
    i.information_request_id AS taydennyspyynto_id,
    CASE
        WHEN i.field_key = 'CUSTOMER' THEN 'Asiakas'
        WHEN i.field_key = 'INVOICING_CUSTOMER' THEN 'Laskutusasiakas'
        WHEN i.field_key = 'GEOMETRY' THEN 'Geometria'
        WHEN i.field_key = 'START_TIME' THEN 'Alkuaika'
        WHEN i.field_key = 'END_TIME' THEN 'Loppuaika'
        WHEN i.field_key = 'IDENTIFICATION_NUMBER' THEN 'Asiointitunnus'
        WHEN i.field_key = 'CLIENT_APPLICATION_KIND' THEN 'Asiakasjärjestelmän hakemuslaji'
        WHEN i.field_key = 'APPLICATION_KIND' THEN 'Hakemuslaji'
        WHEN i.field_key = 'POSTAL_ADDRESS' THEN 'Osoite'
        WHEN i.field_key = 'WORK_DESCRIPTION' THEN 'Työn kuvaus'
        WHEN i.field_key = 'PROPERTY_IDENTIFICATION_NUMBER' THEN 'Kiinteistötunnus'
        WHEN i.field_key = 'ATTACHMENT' THEN 'Liite'
    END AS kentta,
    i.description AS kuvaus
FROM allu_operative.information_request_field i
ON CONFLICT (id) DO UPDATE SET
    taydennyspyynto_id = EXCLUDED.taydennyspyynto_id,
    kentta = EXCLUDED.kentta,
    kuvaus = EXCLUDED.kuvaus
;

