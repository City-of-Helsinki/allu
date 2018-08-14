INSERT INTO allureport.sopimus (
  id,
  hakemus_id,
  luontiaika,
  vastausaika,
  status,
  hylkays_syy,
  allekirjoittaja,
  puitesopimus,
  sopimus_liitteena
)
SELECT
    c.id AS id,
    c.application_id AS hakemus_id,
    c.creation_time AS luontiaika,
    c.response_time AS vastausaika,
    CASE
        WHEN c.status = 'UNPAID_DEPOSIT' THEN 'Asetettu'
        WHEN c.status = 'PAID_DEPOSIT' THEN 'Maksettu'
        WHEN c.status = 'RETURNED_DEPOSIT' THEN 'Palautettu'
    END AS status,
    c.rejection_reason AS hylkays_syy,
    c.signer AS allekirjoittaja,
    c.frame_agreement_exists AS puitesopimus,
    c.contract_as_attachment AS sopimus_liitteena
FROM allu_operative.contract c
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    luontiaika = EXCLUDED.luontiaika,
    vastausaika = EXCLUDED.vastausaika,
    status = EXCLUDED.status,
    hylkays_syy = EXCLUDED.hylkays_syy,
    allekirjoittaja = EXCLUDED.allekirjoittaja,
    puitesopimus = EXCLUDED.puitesopimus,
    sopimus_liitteena = EXCLUDED.sopimus_liitteena
;

DELETE FROM allureport.sopimus s WHERE NOT EXISTS (SELECT id FROM allu_operative.contract oc WHERE oc.id = s.id);
