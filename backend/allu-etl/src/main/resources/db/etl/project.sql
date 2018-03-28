INSERT INTO allureport.hanke
SELECT
		p.id AS id,
		p.parent_id AS liittyy_hankkeeseen,
		p.name AS nimi,
		p.start_time AS alkuaika,
		p.end_time AS loppuaika,
		p.city_districts AS kaupunginosa_id,
		p.customer_reference AS asiakkaan_viite,
		p.additional_info AS lisatiedot
FROM allu_operative.project p
ON CONFLICT (id) DO UPDATE SET
		liittyy_hankkeeseen = EXCLUDED.liittyy_hankkeeseen,
		nimi = EXCLUDED.nimi,
		alkuaika = EXCLUDED.alkuaika,
		loppuaika = EXCLUDED.loppuaika,
		kaupunginosa_id = EXCLUDED.kaupunginosa_id,
		asiakkaan_viite = EXCLUDED.asiakkaan_viite,
		lisatiedot = EXCLUDED.lisatiedot
;