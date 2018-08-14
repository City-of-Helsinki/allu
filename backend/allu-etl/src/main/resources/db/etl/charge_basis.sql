INSERT INTO allureport.laskuperuste (
  id,
  hakemus_id,
  tunniste,
  viitattu_tunniste,
  manuaalisesti_asetettu,
  tyyppi,
  yksikko,
  maara,
  teksti,
  perusteet,
  yksikkohinta,
  kokonaishinta
)
SELECT
    c.id AS id,
    c.application_id AS hakemus_id,
    c.tag AS tunniste,
    c.referred_tag AS viitattu_tunniste,
    c.manually_set AS manuaalisesti_asetettu,
    CASE
        WHEN c.type = 'CALCULATED' THEN 'Laskettu'
        WHEN c.type = 'NEGLIGENCE_FEE' THEN 'Laiminlyöntimaksu'
        WHEN c.type = 'ADDITIONAL_FEE' THEN 'Ylimääräinen maksu'
        WHEN c.type = 'DISCOUNT' THEN 'Alennus'
        WHEN c.type = 'AREA_USAGE_FEE' THEN 'Alueenkäyttömaksu'
    END AS tyyppi,
    CASE
        WHEN c.unit = 'PIECE' THEN 'kpl'
        WHEN c.unit = 'SQUARE_METER' THEN 'm²'
        WHEN c.unit = 'HOUR' THEN 't'
        WHEN c.unit = 'DAY' THEN 'pv'
        WHEN c.unit = 'WEEK' THEN 'vk'
        WHEN c.unit = 'MONTH' THEN 'kk'
        WHEN c.unit = 'YEAR' THEN 'v'
        WHEN c.unit = 'PERCENT' THEN '%'
    END AS yksikko,
    c.quantity AS maara,
    c.text AS teksti,
    c.explanation AS perusteet,
    c.unit_price AS yksikkohinta,
    c.net_price AS kokonaishinta
FROM allu_operative.charge_basis c
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    tunniste = EXCLUDED.tunniste,
    viitattu_tunniste = EXCLUDED.viitattu_tunniste,
    manuaalisesti_asetettu = EXCLUDED.manuaalisesti_asetettu,
    tyyppi = EXCLUDED.tyyppi,
    yksikko = EXCLUDED.yksikko,
    maara = EXCLUDED.maara,
    teksti = EXCLUDED.teksti,
    perusteet = EXCLUDED.perusteet,
    yksikkohinta = EXCLUDED.yksikkohinta,
    kokonaishinta = EXCLUDED.kokonaishinta
;

DELETE FROM allureport.laskuperuste l WHERE NOT EXISTS (SELECT id FROM allu_operative.charge_basis oc WHERE oc.id = l.id);
