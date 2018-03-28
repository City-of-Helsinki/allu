INSERT INTO allureport.sijainti
SELECT
    l.id AS id,
    l.application_id AS hakemus_id,
    l.location_key AS sijainti_avain,
    l.location_version AS sijainti_versio,
    l.start_time AS alku_aika,
    l.end_time AS loppu_aika,
    l.additional_info AS lisatiedot,
    p.street_address AS katuosoite,
    p.postal_code AS postinumero,
    p.city AS postitoimipaikka,
    l.area AS pinta_ala,
    l.area_override AS syotetty_pinta_ala,
    l.city_district_id AS kaupunginosa_id,
    l.city_district_id_override AS syotetty_kaupunginosa_id,
    l.payment_tariff AS maksuluokka,
    l.payment_tariff_override AS syotetty_maksuluokka,
    l.underpass AS altakuljettava
FROM allu_operative.location l
LEFT JOIN allu_operative.postal_address p on l.postal_address_id = p.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    sijainti_avain = EXCLUDED.sijainti_avain,
    sijainti_versio = EXCLUDED.sijainti_versio,
    alku_aika = EXCLUDED.alku_aika,
    loppu_aika = EXCLUDED.loppu_aika,
    lisatiedot = EXCLUDED.lisatiedot,
    katuosoite = EXCLUDED.katuosoite,
    postinumero = EXCLUDED.postinumero,
    postitoimipaikka = EXCLUDED.postitoimipaikka,
    pinta_ala = EXCLUDED.pinta_ala,
    syotetty_pinta_ala = EXCLUDED.syotetty_pinta_ala,
    kaupunginosa_id = EXCLUDED.kaupunginosa_id,
    syotetty_kaupunginosa_id = EXCLUDED.syotetty_kaupunginosa_id,
    maksuluokka = EXCLUDED.maksuluokka,
    syotetty_maksuluokka = EXCLUDED.syotetty_maksuluokka,
    altakuljettava = EXCLUDED.altakuljettava
;