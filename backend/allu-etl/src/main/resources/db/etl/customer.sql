INSERT INTO allureport.asiakas (
  id,
  katuosoite,
  postinumero,
  postitoimipaikka,
  tyyppi,
  nimi,
  tunniste,
  ovt,
  operaattoritunnus,
  email,
  puhelin,
  sap_asiakas_numero,
  laskutuskielto,
  laskutusasiakas,
  maa,
  aktiivinen
)
SELECT
    c.id AS id,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN p.street_address
        ELSE ''
    END AS katuosoite,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN p.postal_code
        ELSE ''
    END AS postinumero,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN  p.city
        ELSE ''
    END AS postitoimipaikka,
    CASE
        WHEN c.type = 'COMPANY' THEN 'Yritys'
        WHEN c.type = 'ASSOCIATION' THEN 'Yhdistys'
        WHEN c.type = 'PERSON' THEN 'Yksityishenkilö'
        WHEN c.type = 'PROPERTY' THEN 'Kiinteistö'
        ELSE 'Muu'
    END as tyyppi,
    CASE
        WHEN c.type = 'PERSON' THEN 'Yksityishenkilö'
        WHEN c.type = 'OTHER' THEN  'Muu'
        ELSE  c.name
    END AS nimi,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN  c.registry_key
        ELSE ''
    END AS tunniste,
    c.ovt AS ovt,
    c.invoicing_operator as operaattoritunnus,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN  c.email
        ELSE ''
    END AS email,
    CASE
        WHEN (c.type <> 'PERSON' AND c.type <> 'OTHER') THEN  c.phone
        ELSE ''
    END AS puhelin,
    c.sap_customer_number AS sap_asiakas_numero,
    c.invoicing_prohibited AS laskutuskielto,
    c.invoicing_only AS laskutusasiakas,
    cs.description AS maa,
    c.is_active AS aktiivinen
FROM allu_operative.customer c
LEFT JOIN
  allu_operative.postal_address p ON c.postal_address_id = p.id
LEFT JOIN
  allu_operative.codeset cs on c.country_id = cs.id
ON CONFLICT (id) DO UPDATE SET
    katuosoite = EXCLUDED.katuosoite,
    postinumero = EXCLUDED.postinumero,
    postitoimipaikka = EXCLUDED.postitoimipaikka,
    tyyppi = EXCLUDED.tyyppi,
    nimi = EXCLUDED.nimi,
    tunniste = EXCLUDED.tunniste,
    ovt = EXCLUDED.ovt,
    operaattoritunnus = EXCLUDED.operaattoritunnus,
    email  = EXCLUDED.email,
    puhelin = EXCLUDED.puhelin,
    sap_asiakas_numero = EXCLUDED.sap_asiakas_numero,
    laskutuskielto = EXCLUDED.laskutuskielto,
    laskutusasiakas = EXCLUDED.laskutusasiakas,
    maa = EXCLUDED.maa,
    aktiivinen = EXCLUDED.aktiivinen
;

DELETE FROM allureport.asiakas a WHERE NOT EXISTS (SELECT id FROM allu_operative.customer oc WHERE oc.id = a.id);
