WITH tunnisteet (en, fi) AS (
    VALUES
        ('ADDITIONAL_INFORMATION_REQUESTED', 'Täydennyspyyntö lähetetty'),
        ('STATEMENT_REQUESTED', 'Lausunnolla'),
        ('DEPOSIT_REQUESTED', 'Vakuus määritetty'),
        ('DEPOSIT_PAID', 'Vakuus suoritettu'),
        ('PRELIMINARY_SUPERVISION_REQUESTED', 'Aloitusvalvontapyyntö lähetetty'),
        ('PRELIMINARY_SUPERVISION_DONE', 'Aloitusvalvonta suoritettu'),
        ('PRELIMINARY_SUPERVISION_REJECTED', 'Aloitusvalvonta hylätty'),
        ('SUPERVISION_REQUESTED', 'Valvontapyyntö lähetetty'),
        ('SUPERVISION_REJECTED', 'Valvonta hylätty'),
        ('SUPERVISION_DONE', 'Valvonta suoritettu'),
        ('WAITING', 'Odottaa lisätietoa'),
        ('COMPENSATION_CLARIFICATION', 'Hyvitysselvitys'),
        ('PAYMENT_BASIS_CORRECTION', 'Maksuperusteet korjattava'),
        ('OPERATIONAL_CONDITION_REPORTED', 'Toiminnallinen kunto ilmoitettu'),
        ('OPERATIONAL_CONDITION_ACCEPTED', 'Toiminnallinen kunto hyväksytty'),
        ('OPERATIONAL_CONDITION_REJECTED', 'Toiminnallinen kunto hylätty'),
        ('FINAL_SUPERVISION_REQUESTED', 'Loppuvalvontapyyntö lähetetty'),
        ('FINAL_SUPERVISION_ACCEPTED', 'Loppuvalvonta hyväksytty'),
        ('FINAL_SUPERVISION_REJECTED', 'Loppuvalvonta hylätty'),
        ('SAP_ID_MISSING', 'Laskutettavan SAP-tunnus puuttuu'),
        ('DECISION_NOT_SENT', 'Päätös lähettämättä')
)
INSERT INTO allureport.hakemustunniste
SELECT
    t.id AS id,
    t.application_id AS hakemus_id,
    u.user_name AS lisaaja,
    tu.fi AS tyyppi,
    t.creation_time AS luonti_aika
FROM allu_operative.application_tag t
LEFT JOIN allu_operative.user u on t.added_by = u.id
LEFT JOIN tunnisteet tu ON t.type = tu.en
ON CONFLICT (id) DO UPDATE SET
    hakemus_id = EXCLUDED.hakemus_id,
    lisaaja = EXCLUDED.lisaaja,
    tyyppi = EXCLUDED.tyyppi,
    luonti_aika = EXCLUDED.luonti_aika
;