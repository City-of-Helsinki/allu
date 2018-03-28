INSERT INTO allureport.lasku
SELECT
    i.id AS id,
    i.application_id AS hakemus_id,
    CASE
        WHEN ir.type = 'COMPANY' THEN 'Yritys'
        WHEN ir.type = 'ASSOCIATION' THEN 'Yhdistys'
        WHEN ir.type = 'PERSON' THEN 'Yksityishenkilö'
        WHEN ir.type = 'PROPERTY' THEN 'Kiinteistö'
        ELSE 'Muu'
    END as asiakas_tyyppi,
    CASE
        WHEN ir.type = 'PERSON' THEN 'Yksityishenkilö'
        WHEN ir.type = 'OTHER' THEN  'Muu'
        ELSE ir.name
    END AS asiakas_nimi,
    ir.ovt AS asiakas_ovt,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN  ir.registry_key
        ELSE ''
    END AS asiakas_tunniste,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN  ir.email
        ELSE ''
    END AS asiakas_email,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN  ir.phone
        ELSE ''
    END AS asiakas_puhelin,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN ir.street_address
        ELSE ''
    END AS asiakas_katuosoite,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN ir.postal_code
        ELSE ''
    END AS asiakas_postinumero,
    CASE
        WHEN (ir.type <> 'PERSON' AND ir.type <> 'OTHER') THEN ir.city
        ELSE ''
    END AS asiakas_postitoimipaikka,
    i.invoicable_time AS laskutettava_aika,
    i.invoiced AS laskutettu,
    i.sap_id_pending AS sap_tunnus_puuttuu
FROM allu_operative.invoice i
LEFT JOIN allu_operative.invoice_recipient ir ON i.recipient_id = ir.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    asiakas_tyyppi = EXCLUDED.asiakas_tyyppi,
    asiakas_nimi = EXCLUDED.asiakas_nimi,
    asiakas_ovt = EXCLUDED.asiakas_ovt,
    asiakas_tunniste = EXCLUDED.asiakas_tunniste,
    asiakas_email = EXCLUDED.asiakas_email,
    asiakas_puhelin = EXCLUDED.asiakas_puhelin,
    asiakas_katuosoite = EXCLUDED.asiakas_katuosoite,
    asiakas_postinumero = EXCLUDED.asiakas_postinumero,
    asiakas_postitoimipaikka = EXCLUDED.asiakas_postitoimipaikka,
    laskutettava_aika = EXCLUDED.laskutettava_aika,
    laskutettu = EXCLUDED.laskutettu,
    sap_tunnus_puuttuu = EXCLUDED.sap_tunnus_puuttuu
;