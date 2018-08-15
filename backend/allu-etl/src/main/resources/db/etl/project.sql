INSERT INTO allureport.hanke (
  id,
  liittyy_hankkeeseen,
  nimi,
  alkuaika,
  loppuaika,
  kaupunginosa_id,
  asiakkaan_viite,
  lisatiedot,
  asiakas_id,
  tunniste,
  lisaaja
)
SELECT
		p.id AS id,
		p.parent_id AS liittyy_hankkeeseen,
		p.name AS nimi,
		p.start_time AS alkuaika,
		p.end_time AS loppuaika,
		p.city_districts AS kaupunginosa_id,
		p.customer_reference AS asiakkaan_viite,
		p.additional_info AS lisatiedot,
		p.customer_id AS asiakas_id,
		p.identifier AS tunniste,
		u.user_name AS lisaaja
FROM allu_operative.project p
LEFT JOIN allu_operative.user u ON p.creator_id = u.id
ON CONFLICT (id) DO UPDATE SET
    liittyy_hankkeeseen = EXCLUDED.liittyy_hankkeeseen,
    nimi = EXCLUDED.nimi,
    alkuaika = EXCLUDED.alkuaika,
    loppuaika = EXCLUDED.loppuaika,
    kaupunginosa_id = EXCLUDED.kaupunginosa_id,
    asiakkaan_viite = EXCLUDED.asiakkaan_viite,
    lisatiedot = EXCLUDED.lisatiedot,
    asiakas_id = EXCLUDED.asiakas_id,
    tunniste = EXCLUDED.tunniste,
    lisaaja = EXCLUDED.lisaaja
;
