INSERT INTO allureport.laskurivi (
  id,
  lasku_id,
  rivinumero,
  yksikko,
  maara,
  teksti,
  perusteet,
  yksikkohinta,
  kokonaishinta
)
SELECT
    i.id AS id,
    i.invoice_id AS lasku_id,
    i.row_number AS rivinumero,
    CASE
        WHEN i.unit = 'PIECE' THEN 'kpl'
        WHEN i.unit = 'SQUARE_METER' THEN 'm²'
        WHEN i.unit = 'HOUR' THEN 't'
        WHEN i.unit = 'DAY' THEN 'pv'
        WHEN i.unit = 'WEEK' THEN 'vk'
        WHEN i.unit = 'MONTH' THEN 'kk'
        WHEN i.unit = 'YEAR' THEN 'v'
        WHEN i.unit = 'PERCENT' THEN '%'
    END AS yksikko,
    i.quantity AS maara,
    i.text AS teksti,
    i.explanation AS perusteet,
    i.unit_price AS yksikkohinta,
    i.net_price AS kokonaishinta
FROM allu_operative.invoice_row i
ON CONFLICT (id) DO UPDATE SET
    lasku_id = EXCLUDED.lasku_id,
    rivinumero = EXCLUDED.rivinumero,
    yksikko = EXCLUDED.yksikko,
    maara = EXCLUDED.maara,
    teksti = EXCLUDED.teksti,
    perusteet = EXCLUDED.perusteet,
    yksikkohinta = EXCLUDED.yksikkohinta,
    kokonaishinta = EXCLUDED.kokonaishinta
;

