INSERT INTO allureport.muutoshistoria
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
    END AS muutostyyppi,
    CASE
        WHEN c.new_status = 'PRE_RESERVED' THEN 'Alustava varaus'
        WHEN c.new_status = 'PENDING' THEN 'Vireillä'
        WHEN c.new_status = 'HANDLING' THEN 'Käsittelyssä'
        WHEN c.new_status = 'RETURNED_TO_PREPARATION' THEN 'Palautettu käsittelyyn'
        WHEN c.new_status = 'DECISION_MAKING' THEN 'Odottaa päätöstä'
        WHEN c.new_status = 'DECISION' THEN 'Päätetty'
        WHEN c.new_status = 'REJECTED' THEN 'Hylätty päätös'
        WHEN c.new_status = 'FINISHED' THEN 'Valmis'
        WHEN c.new_status = 'CANCELLED' THEN 'Peruttu'
        WHEN c.new_status = 'REPLACED' THEN 'Korvattu'
        WHEN c.new_status = 'ARCHIVED' THEN 'Arkistoitu'
        ELSE NULL
    END AS uusi_tila,
    c.change_time AS muutos_aika
FROM allu_operative.change_history c
LEFT JOIN allu_operative.user u ON c.user_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    asiakas_id = EXCLUDED.asiakas_id,
    kayttaja = EXCLUDED.kayttaja,
    muutostyyppi = EXCLUDED.muutostyyppi,
    uusi_tila = EXCLUDED.uusi_tila,
    muutos_aika = EXCLUDED.muutos_aika
;