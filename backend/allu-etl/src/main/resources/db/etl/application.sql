INSERT INTO allureport.hakemus (
  id,
  hakemuksen_tunnus,
  hanke_id,
  nimi,
  kasittelija,
  omistaja,
  tila,
  tyyppi,
  luontiaika,
  alkuaika,
  loppuaika,
  toistuvuuden_loppuaika,
  paatoksen_julkisuus,
  paatosaika,
  paatoksen_tekija,
  laskettu_hinta,
  ei_laskutettava,
  ei_laskutettava_peruste,
  laskutusasiakas_id,
  laskutettu,
  korvaava_hakemus_id,
  asiakkaan_viite,
  laskutuspaiva,
  asiointitunnus,
  asiakasjarjestelma_kayttaja,
  hakemus_saapunut,
  laskutusjakson_pituus
)
SELECT
    a.id AS id,
    a.application_id AS hakemuksen_tunnus,
    a.project_id AS hanke_id,
    a.name AS nimi,
    h.user_name AS kasittelija,
    o.user_name AS omistaja,
    CASE
        WHEN a.status = 'PENDING_CLIENT' THEN 'Vireillä asiakasjärjestelmässä'
        WHEN a.status = 'PRE_RESERVED' THEN 'Alustava varaus'
        WHEN a.status = 'PENDING' THEN 'Hakemus saapunut'
        WHEN a.status = 'WAITING_INFORMATION' THEN 'Odottaa täydennystä'
        WHEN a.status = 'INFORMATION_RECEIVED' THEN 'Täydennys vastaanotettu'
        WHEN a.status = 'HANDLING' THEN 'Käsittelyssä'
        WHEN a.status = 'RETURNED_TO_PREPARATION' THEN 'Palautettu käsittelyyn'
        WHEN a.status = 'WAITING_CONTRACT_APPROVAL' THEN 'Odottaa sopimusta'
        WHEN a.status = 'DECISIONMAKING' THEN 'Odottaa päätöstä'
        WHEN a.status = 'DECISION' THEN 'Päätetty'
        WHEN a.status = 'REJECTED' THEN 'Hylätty päätös'
        WHEN a.status = 'OPERATIONAL_CONDITION' THEN 'Toiminnallinen kunto'
        WHEN a.status = 'FINISHED' THEN 'Valmis'
        WHEN a.status = 'CANCELLED' THEN 'Peruttu'
        WHEN a.status = 'REPLACED' THEN 'Korvattu'
        WHEN a.status = 'ARCHIVED' THEN 'Arkistoitu'
        WHEN a.status = 'NOTE' THEN 'Muistiinpano'
        WHEN a.status = 'TERMINATED' THEN 'Irtisanottu'
        WHEN a.status = 'ANONYMIZED' THEN 'Anonymisoitu'
        ELSE 'NA'
    END AS tila,
    CASE
        WHEN a.type = 'EXCAVATION_ANNOUNCEMENT' THEN 'Kaivuilmoitus'
        WHEN a.type = 'AREA_RENTAL' THEN 'Aluevuokraus'
        WHEN a.type = 'TEMPORARY_TRAFFIC_ARRANGEMENTS' THEN 'Tilapäinen liikennejärjestely'
        WHEN a.type = 'CABLE_REPORT' THEN 'Johtoselvitys'
        WHEN a.type = 'PLACEMENT_CONTRACT' THEN 'Sijoitussopimus'
        WHEN a.type = 'EVENT' THEN 'Tapahtuma'
        WHEN a.type = 'SHORT_TERM_RENTAL' THEN 'Lyhytaikainen maanvuokraus'
        WHEN a.type = 'NOTE' THEN 'Muistiinpano'
    END AS tyyppi,
    a.creation_time AS luontiaika,
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
    a.invoicing_date AS laskutuspaiva,
    a.identification_number AS asiointitunnus,
    eu.user_name AS asiakasjarjestelma_kayttaja,
    a.received_time AS hakemus_saapunut,
    a.invoicing_period_length AS laskutusjakson_pituus
FROM allu_operative.application a
LEFT JOIN allu_operative.user h ON a.handler = h.id
LEFT JOIN allu_operative.user o ON a.owner = o.id
LEFT JOIN allu_operative.user d ON a.decision_maker = d.id
LEFT JOIN allu_operative.user eu ON a.external_owner_id = eu.id
ON CONFLICT (id) DO UPDATE SET
    hakemuksen_tunnus = EXCLUDED.hakemuksen_tunnus,
    hanke_id = EXCLUDED.hanke_id,
    nimi = EXCLUDED.nimi,
    kasittelija = EXCLUDED.kasittelija,
    omistaja = EXCLUDED.omistaja,
    tila = EXCLUDED.tila,
    tyyppi = EXCLUDED.tyyppi,
    luontiaika = EXCLUDED.luontiaika,
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
    laskutuspaiva = EXCLUDED.laskutuspaiva,
    asiointitunnus = EXCLUDED.asiointitunnus,
    asiakasjarjestelma_kayttaja = EXCLUDED.asiakasjarjestelma_kayttaja,
    hakemus_saapunut = EXCLUDED.hakemus_saapunut,
    laskutusjakson_pituus = EXCLUDED.laskutusjakson_pituus
;

INSERT INTO allureport.tapahtuma (
  hakemus_id,
  ehdot,
  tapahtuman_luonne,
  kuvaus,
  www_sivu,
  tapahtuman_alkuaika,
  tapahtuman_loppuaika,
  yleisomaara,
  osallistumismaksu,
  eko_kompassi,
  elintarvikemyynti,
  elintarviketoimijat,
  rakenteiden_neliomaara,
  rakenteiden_kuvaus,
  tapahtuma_ajan_poikkeukset
)
SELECT
    a.id AS hakemus_id,
    a.extension ->> 'terms' AS ehdot,
    CASE
        WHEN a.extension ->> 'nature' = 'PUBLIC_FREE' THEN 'Avoin'
        WHEN a.extension ->> 'nature' = 'PUBLIC_NONFREE' THEN 'Maksullinen'
        WHEN a.extension ->> 'nature' = 'CLOSED' THEN 'Suljettu'
    END AS tapahtuman_luonne,
    a.extension ->> 'description' AS kuvaus,
    a.extension ->> 'url' AS www_sivu,
    TO_TIMESTAMP((a.extension ->> 'eventStartTime')::float) AS tapahtuman_alkuaika,
    TO_TIMESTAMP((a.extension ->> 'eventEndTime')::float) AS tapahtuman_loppuaika,
    (a.extension ->> 'attendees')::integer AS yleisomaara,
    (a.extension ->> 'entryFee')::integer AS osallistumismaksu,
    (a.extension ->> 'ecoCompass')::boolean AS eko_kompassi,
    (a.extension ->> 'foodSales')::boolean AS elintarvikemyynti,
    a.extension ->> 'foodProviders' AS elintarviketoimijat,
    (a.extension ->> 'structureArea')::float AS rakenteiden_neliomaara,
    a.extension ->> 'structureDescription' AS rakenteiden_kuvaus,
    a.extension ->> 'timeExceptions' AS tapahtuma_ajan_poikkeukset
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
    elintarviketoimijat = EXCLUDED.elintarviketoimijat,
    rakenteiden_neliomaara = EXCLUDED.rakenteiden_neliomaara,
    rakenteiden_kuvaus = EXCLUDED.rakenteiden_kuvaus,
    tapahtuma_ajan_poikkeukset = EXCLUDED.tapahtuma_ajan_poikkeukset
;

INSERT INTO allureport.lyhyt_maanvuokraus (
  hakemus_id,
  ehdot,
  kuvaus,
  kaupallinen,
  laskutettava_myyntialue,
  irtisanomispaiva
)
SELECT
    a.id AS hakemus_id,
    a.extension ->> 'terms' AS ehdot,
    a.extension ->> 'description' AS kuvaus,
    (a.extension ->> 'commercial')::boolean AS kaupallinen,
    (a.extension ->> 'billableSalesArea')::boolean AS laskutettava_myyntialue,
    t.expiration_time AS irtisanomispaiva
FROM allu_operative.application a
LEFT JOIN allu_operative.termination t ON t.application_id = a.id
WHERE a.type = 'SHORT_TERM_RENTAL'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    kuvaus = EXCLUDED.kuvaus,
    kaupallinen = EXCLUDED.kaupallinen,
    laskutettava_myyntialue = EXCLUDED.laskutettava_myyntialue,
    irtisanomispaiva = EXCLUDED.irtisanomispaiva
;

INSERT INTO allureport.muistiinpano (
  hakemus_id,
  ehdot,
  kuvaus
)
SELECT
    a.id AS hakemus_id,
    a.extension ->> 'terms' AS ehdot,
    a.extension ->> 'description' AS kuvaus
FROM allu_operative.application a
WHERE a.type = 'NOTE'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    kuvaus = EXCLUDED.kuvaus
;

INSERT INTO allureport.liikennejarjestely (
  hakemus_id,
  ehdot,
  tyon_tarkoitus,
  liikennejarjestelyt,
  liikennehaitta
)
SELECT
  a.id AS hakemus_id,
  a.extension ->> 'terms' AS ehdot,
  a.extension ->> 'workPurpose' AS tyon_tarkoitus,
  a.extension ->> 'trafficArrangements' AS liikennejarjestelyt,
  CASE
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'NO_IMPEDIMENT' THEN 'Ei haittaa'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'SIGNIFICANT_IMPEDIMENT' THEN 'Merkittävä haitta'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'IMPEDIMENT_FOR_HEAVY_TRAFFIC' THEN 'Haittaa raskasta liikennettä'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'INSIGNIFICANT_IMPEDIMENT' THEN 'Vähäinen haitta'
  END AS liikennehaitta
FROM allu_operative.application a
WHERE a.type = 'TEMPORARY_TRAFFIC_ARRANGEMENTS'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    tyon_tarkoitus = EXCLUDED.tyon_tarkoitus,
    liikennejarjestelyt = EXCLUDED.liikennejarjestelyt,
    liikennehaitta = EXCLUDED.liikennehaitta
;

INSERT INTO allureport.sijoitussopimus (
  hakemus_id,
  ehdot,
  kiinteistotunnus,
  tyonkuvaus,
  sopimusteksti,
  irtisanomispaiva,
  pykala,
  paatoksen_perustelut
)
SELECT
  a.id AS hakemus_id,
  a.extension ->> 'terms' AS ehdot,
  a.extension ->> 'propertyIdentificationNumber' AS kiinteistotunnus,
  a.extension ->> 'additionalInfo' AS tyonkuvaus,
  a.extension ->> 'contractText' AS sopimusteksti,
  t.expiration_time AS irtisanomispaiva,
  (a.extension ->> 'sectionNumber')::integer AS pykala,
  a.extension ->> 'rationale' AS paatoksen_perustelut
FROM allu_operative.application a
LEFT JOIN allu_operative.termination t ON t.application_id = a.id
WHERE a.type = 'PLACEMENT_CONTRACT'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    kiinteistotunnus = EXCLUDED.kiinteistotunnus,
    tyonkuvaus = EXCLUDED.tyonkuvaus,
    sopimusteksti = EXCLUDED.sopimusteksti,
    irtisanomispaiva = EXCLUDED.irtisanomispaiva,
    pykala = EXCLUDED.pykala,
    paatoksen_perustelut = EXCLUDED.paatoksen_perustelut
;

INSERT INTO allureport.johtoselvitys (
  hakemus_id,
  ehdot,
  tyon_kuvaus,
  karttaotteiden_maara,
  rakentaminen,
  kunnossapito,
  hatatyo,
  tontti_kiinteisto_liitos,
  voimassaoloaika
)
SELECT
  a.id AS hakemus_id,
  a.extension ->> 'terms' AS ehdot,
  a.extension ->> 'workDescription' AS tyonkuvaus,
  (a.extension ->> 'mapExtractCount')::integer AS karttaotteiden_maara,
  (a.extension ->> 'constructionWork')::boolean AS rakentaminen,
  (a.extension ->> 'maintenanceWork')::boolean AS kunnossapito,
  (a.extension ->> 'emergencyWork')::boolean AS hatatyo,
  (a.extension ->> 'propertyConnectivity')::boolean AS tontti_kiinteisto_liitos,
  TO_TIMESTAMP((a.extension ->> 'validityTime')::float) AS voimassaoloaika
FROM allu_operative.application a
WHERE a.type = 'CABLE_REPORT'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
	  tyon_kuvaus = EXCLUDED.tyon_kuvaus,
	  karttaotteiden_maara = EXCLUDED.karttaotteiden_maara,
	  rakentaminen = EXCLUDED.rakentaminen,
	  kunnossapito = EXCLUDED.kunnossapito,
	  hatatyo = EXCLUDED.hatatyo,
	  tontti_kiinteisto_liitos = EXCLUDED.tontti_kiinteisto_liitos,
	  voimassaoloaika = EXCLUDED.voimassaoloaika
;


INSERT INTO allureport.kaivuilmoitus (
  hakemus_id,
  ehdot,
  pks_kortti,
  rakentaminen,
  kunnossapito,
  hatatyo,
  tontti_kiinteisto_liitos,
  omavalvonta,
  toiminnallinen_kunto,
  tyo_valmis,
  luvaton_alkuaika,
  luvaton_loppuaika,
  takuu_paattyy,
  asiakas_alkuaika,
  asiakas_loppuaika,
  asiakas_toiminnallinen_kunto,
  asiakas_tyo_valmis,
  toiminnallinen_kunto_ilmoitettu,
  tyo_valmis_ilmoitettu,
  tyon_tarkoitus,
  liikennejarjestelyt,
  liikennehaitta,
  tiiveys_ja_kantavuusmittaus,
  paallysteen_laadunvarmistuskoe,
  lisatiedot,
  johtoselvitykset,
  sijoitussopimukset
)
SELECT
  a.id AS hakemus_id,
  a.extension ->> 'terms' AS ehdot,
  (a.extension ->> 'pksCard')::boolean AS pks_kortti,
  (a.extension ->> 'constructionWork')::boolean AS rakentaminen,
  (a.extension ->> 'maintenanceWork')::boolean AS kunnossapito,
  (a.extension ->> 'emergencyWork')::boolean AS hatatyo,
  (a.extension ->> 'propertyConnectivity')::boolean AS tontti_kiinteisto_liitos,
  (a.extension ->> 'selfSupervision')::boolean AS omavalvonta,
  TO_TIMESTAMP((a.extension ->> 'winterTimeOperation')::float) AS toiminnallinen_kunto,
  TO_TIMESTAMP((a.extension ->> 'workFinished')::float) AS tyo_valmis,
  TO_TIMESTAMP((a.extension ->> 'unauthorizedWorkStartTime')::float) AS luvaton_alkuaika,
  TO_TIMESTAMP((a.extension ->> 'unauthorizedWorkEndTime')::float) AS luvaton_loppuaika,
  TO_TIMESTAMP((a.extension ->> 'guaranteeEndTime')::float) AS takuu_paattyy,
  TO_TIMESTAMP((a.extension ->> 'customerStartTime')::float) AS asiakas_alkuaika,
  TO_TIMESTAMP((a.extension ->> 'customerEndTime')::float) AS asiakas_loppuaika,
  TO_TIMESTAMP((a.extension ->> 'customerWinterTimeOperation')::float) AS asiakas_toiminnallinen_kunto,
  TO_TIMESTAMP((a.extension ->> 'customerWorkFinished')::float) AS asiakas_tyo_valmis,
  TO_TIMESTAMP((a.extension ->> 'operationalConditionReported')::float) AS toiminnallinen_kunto_ilmoitettu,
  TO_TIMESTAMP((a.extension ->> 'workFinishedReported')::float) AS tyo_valmis_ilmoitettu,
  a.extension ->> 'workPurpose' AS tyon_tarkoitus,
  a.extension ->> 'trafficArrangements' AS liikennejarjestelyt,
    CASE
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'NO_IMPEDIMENT' THEN 'Ei haittaa'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'SIGNIFICANT_IMPEDIMENT' THEN 'Merkittävä haitta'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'IMPEDIMENT_FOR_HEAVY_TRAFFIC' THEN 'Haittaa raskasta liikennettä'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'INSIGNIFICANT_IMPEDIMENT' THEN 'Vähäinen haitta'
  END AS liikennehaitta,
  (a.extension ->> 'compactionAndBearingCapacityMeasurement')::boolean AS tiiveys_ja_kantavuusmittaus,
  (a.extension ->> 'qualityAssuranceTest')::boolean AS paallysteen_laadunvarmistuskoe,
  a.extension ->> 'additionalInfo' AS lisatiedot,
  a.extension ->> 'cableReports' AS johtoselvitykset,
  a.extension ->> 'placementContracts' AS sijoitussopimukset
FROM allu_operative.application a
WHERE a.type = 'EXCAVATION_ANNOUNCEMENT'
ON CONFLICT (hakemus_id) DO UPDATE SET
	  ehdot = EXCLUDED.ehdot,
	  pks_kortti = EXCLUDED.pks_kortti,
	  rakentaminen = EXCLUDED.rakentaminen,
	  kunnossapito = EXCLUDED.kunnossapito,
	  hatatyo = EXCLUDED.hatatyo,
	  tontti_kiinteisto_liitos = EXCLUDED.tontti_kiinteisto_liitos,
	  omavalvonta = EXCLUDED.omavalvonta,
	  toiminnallinen_kunto = EXCLUDED.toiminnallinen_kunto,
	  tyo_valmis = EXCLUDED.tyo_valmis,
	  luvaton_alkuaika = EXCLUDED.luvaton_alkuaika,
	  luvaton_loppuaika = EXCLUDED.luvaton_loppuaika,
	  takuu_paattyy = EXCLUDED.takuu_paattyy,
	  asiakas_alkuaika = EXCLUDED.asiakas_alkuaika,
	  asiakas_loppuaika = EXCLUDED.asiakas_loppuaika,
	  asiakas_toiminnallinen_kunto = EXCLUDED.asiakas_toiminnallinen_kunto,
	  asiakas_tyo_valmis = EXCLUDED.asiakas_tyo_valmis,
	  toiminnallinen_kunto_ilmoitettu = EXCLUDED.toiminnallinen_kunto_ilmoitettu,
	  tyo_valmis_ilmoitettu = EXCLUDED.tyo_valmis_ilmoitettu,
	  tyon_tarkoitus = EXCLUDED.tyon_tarkoitus,
	  liikennejarjestelyt = EXCLUDED.liikennejarjestelyt,
	  liikennehaitta = EXCLUDED.liikennehaitta,
    tiiveys_ja_kantavuusmittaus = EXCLUDED.tiiveys_ja_kantavuusmittaus,
    paallysteen_laadunvarmistuskoe = EXCLUDED.paallysteen_laadunvarmistuskoe,
    lisatiedot = EXCLUDED.lisatiedot,
    johtoselvitykset = EXCLUDED.johtoselvitykset,
    sijoitussopimukset = EXCLUDED.sijoitussopimukset
;

INSERT INTO allureport.aluevuokraus (
  hakemus_id,
  ehdot,
  pks_kortti,
  haittaa_aiheuttava,
  tyon_tarkoitus,
  lisatiedot,
  liikennejarjestelyt,
  tyo_valmis,
  asiakas_tyo_valmis,
  tyo_valmis_ilmoitettu,
  liikennehaitta
)
SELECT
  a.id AS hakemus_id,
  a.extension ->> 'terms' AS ehdot,
  (a.extension ->> 'pksCard')::boolean AS pks_kortti,
  (a.extension ->> 'majorDisturbance')::boolean AS haittaa_aiheuttava,
  a.extension ->> 'workPurpose' AS tyon_tarkoitus,
  a.extension ->> 'additionalInfo' AS lisatiedot,
  a.extension ->> 'trafficArrangements' AS liikennejarjestelyt,
  TO_TIMESTAMP((a.extension ->> 'workFinished')::float) AS tyo_valmis,
  TO_TIMESTAMP((a.extension ->> 'customerWorkFinished')::float) AS asiakas_tyo_valmis,
  TO_TIMESTAMP((a.extension ->> 'workFinishedReported')::float) AS tyo_valmis_ilmoitettu,
  CASE
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'NO_IMPEDIMENT' THEN 'Ei haittaa'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'SIGNIFICANT_IMPEDIMENT' THEN 'Merkittävä haitta'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'IMPEDIMENT_FOR_HEAVY_TRAFFIC' THEN 'Haittaa raskasta liikennettä'
      WHEN a.extension ->> 'trafficArrangementImpedimentType' = 'INSIGNIFICANT_IMPEDIMENT' THEN 'Vähäinen haitta'
  END AS liikennehaitta
FROM allu_operative.application a
WHERE a.type = 'AREA_RENTAL'
ON CONFLICT (hakemus_id) DO UPDATE SET
    ehdot = EXCLUDED.ehdot,
    pks_kortti = EXCLUDED.pks_kortti,
    haittaa_aiheuttava = EXCLUDED.haittaa_aiheuttava,
    tyon_tarkoitus = EXCLUDED.tyon_tarkoitus,
    lisatiedot = EXCLUDED.lisatiedot,
    liikennejarjestelyt = EXCLUDED.liikennejarjestelyt,
    tyo_valmis = EXCLUDED.tyo_valmis,
    asiakas_tyo_valmis = EXCLUDED.asiakas_tyo_valmis,
    tyo_valmis_ilmoitettu = EXCLUDED.tyo_valmis_ilmoitettu,
    liikennehaitta = EXCLUDED.liikennehaitta
;
