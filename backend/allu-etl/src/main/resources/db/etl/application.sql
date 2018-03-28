INSERT INTO allureport.hakemus
SELECT
    a.id AS id,
    a.application_id AS hakemuksen_tunnus,
    a.project_id AS hanke_id,
    a.name AS nimi,
    h.user_name AS kasittelija,
    o.user_name AS omistaja,
    CASE
	      WHEN a.status = 'PRE_RESERVED' THEN 'Alustava varaus'
	      WHEN a.status = 'PENDING' THEN 'Vireillä'
	      WHEN a.status = 'HANDLING' THEN 'Käsittelyssä'
	      WHEN a.status = 'RETURNED_TO_PREPARATION' THEN 'Palautettu valmisteluun'
	      WHEN a.status = 'DECISION_MAKING' THEN 'Odottaa päätöstä'
	      WHEN a.status = 'DECISION' THEN 'Päätetty'
	      WHEN a.status = 'REJECTED' THEN 'Hylätty päätös'
	      WHEN a.status = 'FINISHED' THEN 'Valmis'
	      WHEN a.status = 'CANCELLED' THEN 'Peruttu'
	      WHEN a.status = 'REPLACED' THEN 'Korvattu'
	      WHEN a.status = 'ARCHIVED' THEN 'Arkistoitu'
        ELSE 'NA'
    END AS tila,
    CASE
        WHEN a.type = 'EXCAVATION_ANNOUNCEMENT' THEN 'Kaivuilmoitus'
        WHEN a.type = 'AREA_RENTAL' THEN 'Aluevuokraus'
        WHEN a.type = 'TEMPORARY_TRAFFIC_ARRANGEMENTS' THEN 'Väliaikainen liikennejärjestely'
        WHEN a.type = 'CABLE_REPORT' THEN 'Johtoselvitys'
        WHEN a.type = 'PLACEMENT_CONTRACT' THEN 'Sijoitussopimus'
        WHEN a.type = 'EVENT' THEN 'Tapahtuma'
        WHEN a.type = 'SHORT_TERM_RENTAL' THEN 'Lyhytaikainen maanvuokraus'
        WHEN a.type = 'NOTE' THEN 'Muistiinpano'
    END AS tyyppi,
    a.creation_time AS luonti_aika,
    a.start_time AS alkuaika,
    a.end_time AS loppuaika,
    a.recurring_end_time AS toistuvuuden_loppuaika,
    CASE
      WHEN a.decision_publicity_type = 'PUBLIC' THEN 'Julkinen'
      WHEN a.decision_publicity_type = 'NON_PUBLIC' THEN 'Ei-julkinen'
      WHEN a.decision_publicity_type = 'CONFIDENTIAL_PARTIALLY' THEN 'Osittain salassa pidettävä'
      WHEN a.decision_publicity_type = 'CONFIDENTIAL' THEN 'Salassa pidettävä'
    END AS paatoksen_julkisuus,
    a.decision_time AS paatosaika,
    d.user_name AS paatoksen_tekija,
    a.calculated_price AS laskettu_hinta,
    a.not_billable AS ei_laskutettava,
    a.not_billable_reason AS ei_laskutettava_peruste,
    a.invoice_recipient_id AS laskutusasiakas_id,
    a.invoiced AS laskutettu,
    a.replaced_by_application_id AS korvaava_hakemus_id,
    a.customer_reference AS asiakkaan_viite,
    a.invoicing_date AS laskutuspaiva
FROM allu_operative.application a
LEFT JOIN allu_operative.user h ON a.handler = h.id
LEFT JOIN allu_operative.user o ON a.owner = o.id
LEFT JOIN allu_operative.user d ON a.decision_maker = d.id
ON CONFLICT (id) DO UPDATE SET
    hakemuksen_tunnus = EXCLUDED.hakemuksen_tunnus,
    hanke_id = EXCLUDED.hanke_id,
    nimi = EXCLUDED.nimi,
    kasittelija = EXCLUDED.kasittelija,
    omistaja = EXCLUDED.omistaja,
    tila = EXCLUDED.tila,
    tyyppi = EXCLUDED.tyyppi,
    luonti_aika = EXCLUDED.luonti_aika,
    alkuaika = EXCLUDED.alkuaika,
    loppuaika = EXCLUDED.loppuaika,
    toistuvuuden_loppuaika = EXCLUDED.toistuvuuden_loppuaika,
    paatoksen_julkisuus = EXCLUDED.paatoksen_julkisuus,
    paatosaika = EXCLUDED.paatosaika,
    paatoksen_tekija = EXCLUDED.paatoksen_tekija,
    laskettu_hinta = EXCLUDED.laskettu_hinta,
    ei_laskutettava = EXCLUDED.ei_laskutettava,
    ei_laskutettava_peruste = EXCLUDED.ei_laskutettava_peruste,
    laskutusasiakas_id = EXCLUDED.laskutusasiakas_id,
    laskutettu = EXCLUDED.laskutettu,
    korvaava_hakemus_id = EXCLUDED.korvaava_hakemus_id,
    asiakkaan_viite = EXCLUDED.asiakkaan_viite,
    laskutuspaiva = EXCLUDED.laskutuspaiva
;

INSERT INTO allureport.tapahtuma
SELECT
    a.id AS hakemus_id,
    a.extension::json ->> 'terms' AS ehdot,
    CASE
        WHEN a.extension::json ->> 'nature' = 'PUBLIC_FREE' THEN 'Avoin'
        WHEN a.extension::json ->> 'nature' = 'PUBLIC_NONFREE' THEN 'Maksullinen'
        WHEN a.extension::json ->> 'nature' = 'CLOSED' THEN 'Suljettu'
    END AS tapahtuman_luonne,
    a.extension::json ->> 'description' AS kuvaus,
    a.extension::json ->> 'url' AS www_sivu,
    TO_TIMESTAMP((a.extension::json ->> 'eventStartTime')::float) AS tapahtuman_alkuaika,
    TO_TIMESTAMP((a.extension::json ->> 'eventEndTime')::float) AS tapahtuman_loppuaika,
    (a.extension::json ->> 'attendees')::integer AS yleisomaara,
    (a.extension::json ->> 'entryFee')::integer AS osallistumismaksu,
    (a.extension::json ->> 'ecoCompass')::boolean AS eko_kompassi,
    (a.extension::json ->> 'foodSales')::boolean AS elintarvikemyynti,
    a.extension::json ->> 'foodProviders' AS elintarvike_toimijat,
    (a.extension::json ->> 'structureArea')::float AS rakenteiden_neliomaara,
    a.extension::json ->> 'structureDescription' AS rakenteiden_kuvaus,
    a.extension::json ->> 'timeExceptions' AS tapahtuma_ajan_poikkeukset
FROM allu_operative.application a
WHERE a.type = 'EVENT'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    tapahtuman_luonne = EXCLUDED.tapahtuman_luonne,
    kuvaus = EXCLUDED.kuvaus,
    www_sivu = EXCLUDED.www_sivu,
    tapahtuman_alkuaika = EXCLUDED.tapahtuman_alkuaika,
    tapahtuman_loppuaika = EXCLUDED.tapahtuman_loppuaika,
    yleisomaara = EXCLUDED.yleisomaara,
    osallistumismaksu = EXCLUDED.osallistumismaksu,
    eko_kompassi = EXCLUDED.eko_kompassi,
    elintarvikemyynti = EXCLUDED.elintarvikemyynti,
    elintarvike_toimijat = EXCLUDED.elintarvike_toimijat,
    rakenteiden_neliomaara = EXCLUDED.rakenteiden_neliomaara,
    rakenteiden_kuvaus = EXCLUDED.rakenteiden_kuvaus,
    tapahtuma_ajan_poikkeukset = EXCLUDED.tapahtuma_ajan_poikkeukset
;

INSERT INTO allureport.lyhyt_maanvuokraus
SELECT
    a.id AS hakemus_id,
    a.extension::json ->> 'terms' AS ehdot,
    a.extension::json ->> 'description' AS kuvaus,
    (a.extension::json ->> 'commercial')::boolean AS kaupallinen,
    (a.extension::json ->> 'largeSalesArea')::boolean AS iso_myyntialue
FROM allu_operative.application a
WHERE a.type = 'SHORT_TERM_RENTAL'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    kuvaus = EXCLUDED.kuvaus,
    kaupallinen = EXCLUDED.kaupallinen,
    iso_myyntialue = EXCLUDED.iso_myyntialue
;

INSERT INTO allureport.muistiinpano
SELECT
    a.id AS hakemus_id,
    a.extension::json ->> 'terms' AS ehdot,
    a.extension::json ->> 'description' AS kuvaus
FROM allu_operative.application a
WHERE a.type = 'NOTE'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    kuvaus = EXCLUDED.kuvaus
;