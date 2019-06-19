INSERT INTO allureport.muutoshistoria (
  id,
  hakemus_id,
  asiakas_id,
  kayttaja,
  muutostyyppi,
  muutostarkenne,
  muutosaika,
  hanke_id
)
SELECT
    c.id AS id,
    c.application_id AS hakemus_id,
    c.customer_id AS asiakas_id,
    u.user_name AS kayttaja,
    CASE 
        WHEN change_type = 'CREATED' THEN 'Luotu'
        WHEN change_type = 'STATUS_CHANGED' THEN 'Siirretty tilaan'
        WHEN change_type = 'CONTENTS_CHANGED' THEN 'Tietoja päivitetty'
        WHEN change_type = 'REPLACED' THEN 'Korvattu'
        WHEN change_type = 'APPLICATION_ADDED' THEN 'Hakemus lisätty'
        WHEN change_type = 'APPLICATION_REMOVED' THEN 'Hakemus poistettu'
        WHEN change_type = 'CUSTOMER_CHANGED' THEN 'Asiakas päivitetty'
        WHEN change_type = 'CONTACT_CHANGED' THEN 'Yhteystieto päivitetty'
        WHEN change_type = 'LOCATION_CHANGED' THEN 'Sijainti päivitetty'
        WHEN change_type = 'OWNER_CHANGED' THEN 'Omistaja päivitetty'
    END AS muutostyyppi,
    CASE
        WHEN c.change_specifier = 'PENDING_CLIENT' THEN 'Vireillä asiakasjärjestelmässä'
        WHEN c.change_specifier = 'PRE_RESERVED' THEN 'Alustava varaus'
        WHEN c.change_specifier = 'PENDING' THEN 'Vireillä'
        WHEN c.change_specifier = 'WAITING_INFORMATION' THEN 'Odottaa täydennystä'
        WHEN c.change_specifier = 'INFORMATION_RECEIVED' THEN 'Täydennys vastaanotettu'
        WHEN c.change_specifier = 'HANDLING' THEN 'Käsittelyssä'
        WHEN c.change_specifier = 'RETURNED_TO_PREPARATION' THEN 'Palautettu käsittelyyn'
        WHEN c.change_specifier = 'WAITING_CONTRACT_APPROVAL' THEN 'Odottaa sopimusta'
        WHEN c.change_specifier = 'DECISIONMAKING' THEN 'Odottaa päätöstä'
        WHEN c.change_specifier = 'DECISION' THEN 'Päätetty'
        WHEN c.change_specifier = 'REJECTED' THEN 'Hylätty päätös'
        WHEN c.change_specifier = 'OPERATIONAL_CONDITION' THEN 'Toiminnallinen kunto'
        WHEN c.change_specifier = 'FINISHED' THEN 'Valmis'
        WHEN c.change_specifier = 'CANCELLED' THEN 'Peruttu'
        WHEN c.change_specifier = 'REPLACED' THEN 'Korvattu'
        WHEN c.change_specifier = 'ARCHIVED' THEN 'Arkistoitu'
        WHEN c.change_specifier = 'NOTE' THEN 'Muistiinpano'
        WHEN c.change_specifier = 'APPLICANT' THEN 'Hakija'
        WHEN c.change_specifier = 'PROPERTY_DEVELOPER' THEN 'Rakennuttaja'
        WHEN c.change_specifier = 'CONTRACTOR' THEN 'Työn suorittaja'
        WHEN c.change_specifier = 'REPRESENTATIVE' THEN 'Asiamies'
        WHEN c.change_specifier = 'INVOICE_RECIPIENT' THEN 'Laskutusasiakas'
        ELSE NULL
    END AS muutostarkenne,
    c.change_time AS muutosaika,
    c.project_id  AS hanke_id
FROM allu_operative.change_history c
LEFT JOIN allu_operative.user u ON c.user_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    asiakas_id = EXCLUDED.asiakas_id,
    hanke_id = EXCLUDED.hanke_id,
    kayttaja = EXCLUDED.kayttaja,
    muutostyyppi = EXCLUDED.muutostyyppi,
    muutostarkenne = EXCLUDED.muutostarkenne,
    muutosaika = EXCLUDED.muutosaika
;

