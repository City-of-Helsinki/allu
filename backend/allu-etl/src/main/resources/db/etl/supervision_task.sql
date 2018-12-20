INSERT INTO allureport.valvontatehtava (
  id,
  hakemus_id,
  tyyppi,
  lisaaja,
  omistaja,
  luontiaika,
  suunniteltu_loppuaika,
  toteutunut_loppuaika,
  status,
  kuvaus,
  tulos,
  sijainti_id
)
SELECT
    s.id AS id,
    s.application_id AS hakemus_id,
    CASE
        WHEN s.type = 'PRELIMINARY_SUPERVISION' THEN 'Aloitusvalvonta'
        WHEN s.type = 'OPERATIONAL_CONDITION' THEN 'Toiminnallisen kunnon valvonta'
        WHEN s.type = 'SUPERVISION' THEN 'Valvonta'
        WHEN s.type = 'FINAL_SUPERVISION' THEN 'Loppuvalvonta'
        WHEN s.type = 'WARRANTY' THEN 'Takuuvalvonta'
        WHEN s.type = 'WORK_TIME_SUPERVISION' THEN 'Työnaikainen valvonta'
    END AS tyyppi,
    c.user_name AS lisaaja,
    o.user_name AS omistaja,
    s.creation_time AS luontiaika,
    s.planned_finishing_time AS suunniteltu_loppuaika,
    s.actual_finishing_time AS toteutunut_loppuaika,
    CASE
        WHEN s.status = 'OPEN' THEN 'Avoin'
        WHEN s.status = 'APPROVED' THEN 'Hyväksytty'
        WHEN s.status = 'REJECTED' THEN 'Hylätty'
        WHEN s.status = 'CANCELLED' THEN 'Peruttu'
    END AS status,
    s.description AS kuvaus,
    s.result AS tulos,
    s.location_id AS sijainti_id
FROM allu_operative.supervision_task s
LEFT JOIN allu_operative.user c ON s.creator_id = c.id
LEFT JOIN allu_operative.user o ON s.owner_id = o.id
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    tyyppi = EXCLUDED.tyyppi,
    lisaaja = EXCLUDED.lisaaja,
    omistaja = EXCLUDED.omistaja,
    luontiaika = EXCLUDED.luontiaika,
    suunniteltu_loppuaika = EXCLUDED.suunniteltu_loppuaika,
    toteutunut_loppuaika = EXCLUDED.toteutunut_loppuaika,
    status = EXCLUDED.status,
    kuvaus = EXCLUDED.kuvaus,
    tulos = EXCLUDED.tulos,
    sijainti_id = EXCLUDED.sijainti_id
;

