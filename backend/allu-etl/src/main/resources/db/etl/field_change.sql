INSERT INTO allureport.kenttamuutos
SELECT
    f.id AS id,
    f.change_history_id AS muutoshistoria_id,
    f.field_name  AS kentan_nimi,
    f.old_value AS vanha_arvo,
    f.new_value AS uusi_arvo
FROM allu_operative.field_change f
ON CONFLICT (id) DO UPDATE SET
    muutoshistoria_id = EXCLUDED.muutoshistoria_id,
    kentan_nimi = EXCLUDED.kentan_nimi,
    vanha_arvo = EXCLUDED.vanha_arvo,
    uusi_arvo = EXCLUDED.uusi_arvo
;