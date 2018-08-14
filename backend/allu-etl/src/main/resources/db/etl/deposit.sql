INSERT INTO allureport.vakuus (
  id,
  hakemus_id,
  maara,
  syy,
  status,
  lisaaja,
  luontiaika
)
SELECT
    d.id AS id,
    d.application_id AS hakemus_id,
    d.amount AS maara,
    d.reason AS syy,
    CASE
        WHEN d.status = 'UNPAID_DEPOSIT' THEN 'Asetettu'
        WHEN d.status = 'PAID_DEPOSIT' THEN 'Maksettu'
        WHEN d.status = 'RETURNED_DEPOSIT' THEN 'Palautettu'
    END AS status,
    u.user_name AS lisaaja,
    d.creation_time AS luontiaika
FROM allu_operative.deposit d
LEFT JOIN allu_operative.user u on d.creator_id = u.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    maara = EXCLUDED.maara,
    syy = EXCLUDED.syy,
    status = EXCLUDED.status,
    lisaaja = EXCLUDED.lisaaja,
    luontiaika = EXCLUDED.luontiaika
;

DELETE FROM allureport.vakuus v WHERE NOT EXISTS (SELECT id FROM allu_operative.deposit od WHERE od.id = v.id);
