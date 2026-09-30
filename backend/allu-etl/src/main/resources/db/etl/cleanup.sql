-- Removes data deleted from operative database
-- Source ids are materialized once into temporary tables (ON COMMIT DROP) to avoid slow
-- per-row anti-joins over the foreign data wrapper. Delete order keeps child rows before
-- parents so foreign keys are never violated.
-- WARNING: this script must be executed as a single unit within one transaction -
-- the ON COMMIT DROP temporary tables rely on it (EtlRunner.runCleanup is @Transactional).

CREATE TEMP TABLE tmp_information_request_field ON COMMIT DROP AS SELECT id FROM allu_operative.information_request_field;
CREATE TEMP TABLE tmp_information_request ON COMMIT DROP AS SELECT id FROM allu_operative.information_request;
CREATE TEMP TABLE tmp_contract ON COMMIT DROP AS SELECT id FROM allu_operative.contract;
CREATE TEMP TABLE tmp_supervision_task ON COMMIT DROP AS SELECT id FROM allu_operative.supervision_task;
CREATE TEMP TABLE tmp_invoice_row ON COMMIT DROP AS SELECT id FROM allu_operative.invoice_row;
CREATE TEMP TABLE tmp_invoice ON COMMIT DROP AS SELECT id FROM allu_operative.invoice;
CREATE TEMP TABLE tmp_field_change ON COMMIT DROP AS SELECT id FROM allu_operative.field_change;
CREATE TEMP TABLE tmp_change_history ON COMMIT DROP AS SELECT id FROM allu_operative.change_history;
CREATE TEMP TABLE tmp_comment ON COMMIT DROP AS SELECT id FROM allu_operative.comment;
CREATE TEMP TABLE tmp_charge_basis ON COMMIT DROP AS SELECT id FROM allu_operative.charge_basis;
CREATE TEMP TABLE tmp_invoicing_period ON COMMIT DROP AS SELECT id FROM allu_operative.invoicing_period;
CREATE TEMP TABLE tmp_location_geometry ON COMMIT DROP AS SELECT id FROM allu_operative.location_geometry;
CREATE TEMP TABLE tmp_location_flids ON COMMIT DROP AS SELECT id FROM allu_operative.location_flids;
CREATE TEMP TABLE tmp_location ON COMMIT DROP AS SELECT id FROM allu_operative.location;
CREATE TEMP TABLE tmp_fixed_location ON COMMIT DROP AS SELECT id FROM allu_operative.fixed_location;
CREATE TEMP TABLE tmp_location_area ON COMMIT DROP AS SELECT id FROM allu_operative.location_area;
CREATE TEMP TABLE tmp_deposit ON COMMIT DROP AS SELECT id FROM allu_operative.deposit;
CREATE TEMP TABLE tmp_application_tag ON COMMIT DROP AS SELECT id FROM allu_operative.application_tag;
CREATE TEMP TABLE tmp_application_customer ON COMMIT DROP AS SELECT id FROM allu_operative.application_customer;
CREATE TEMP TABLE tmp_kind_specifier ON COMMIT DROP AS SELECT id FROM allu_operative.kind_specifier;
CREATE TEMP TABLE tmp_application_kind ON COMMIT DROP AS SELECT id FROM allu_operative.application_kind;
CREATE TEMP TABLE tmp_application ON COMMIT DROP AS SELECT id FROM allu_operative.application;
CREATE TEMP TABLE tmp_project ON COMMIT DROP AS SELECT id FROM allu_operative.project;
CREATE TEMP TABLE tmp_customer ON COMMIT DROP AS SELECT id FROM allu_operative.customer;
CREATE TEMP TABLE tmp_city_district ON COMMIT DROP AS SELECT id FROM allu_operative.city_district;

DELETE FROM allureport.taydennyspyynto_kentta t WHERE NOT EXISTS (SELECT 1 FROM tmp_information_request_field s WHERE s.id = t.id);
DELETE FROM allureport.taydennyspyynto t WHERE NOT EXISTS (SELECT 1 FROM tmp_information_request s WHERE s.id = t.id);
DELETE FROM allureport.sopimus s WHERE NOT EXISTS (SELECT 1 FROM tmp_contract t WHERE t.id = s.id);
DELETE FROM allureport.valvontatehtava v WHERE NOT EXISTS (SELECT 1 FROM tmp_supervision_task s WHERE s.id = v.id);
DELETE FROM allureport.laskurivi l WHERE NOT EXISTS (SELECT 1 FROM tmp_invoice_row s WHERE s.id = l.id);
DELETE FROM allureport.lasku l WHERE NOT EXISTS (SELECT 1 FROM tmp_invoice s WHERE s.id = l.id);
DELETE FROM allureport.kenttamuutos k WHERE NOT EXISTS (SELECT 1 FROM tmp_field_change s WHERE s.id = k.id);
DELETE FROM allureport.muutoshistoria m WHERE NOT EXISTS (SELECT 1 FROM tmp_change_history s WHERE s.id = m.id);
DELETE FROM allureport.kommentti k WHERE NOT EXISTS (SELECT 1 FROM tmp_comment s WHERE s.id = k.id);
DELETE FROM allureport.laskuperuste l WHERE NOT EXISTS (SELECT 1 FROM tmp_charge_basis s WHERE s.id = l.id);
DELETE FROM allureport.laskutusjakso l WHERE NOT EXISTS (SELECT 1 FROM tmp_invoicing_period s WHERE s.id = l.id);
DELETE FROM allureport.sijainti_geometria s WHERE NOT EXISTS (SELECT 1 FROM tmp_location_geometry t WHERE t.id = s.id);
DELETE FROM allureport.sijainti_kiinteasijainti s WHERE NOT EXISTS (SELECT 1 FROM tmp_location_flids t WHERE t.id = s.id);
DELETE FROM allureport.sijainti s WHERE NOT EXISTS (SELECT 1 FROM tmp_location t WHERE t.id = s.id);
DELETE FROM allureport.kiinteasijainti k WHERE NOT EXISTS (SELECT 1 FROM tmp_fixed_location s WHERE s.id = k.id);
DELETE FROM allureport.alue a WHERE NOT EXISTS (SELECT 1 FROM tmp_location_area s WHERE s.id = a.id);
DELETE FROM allureport.vakuus v WHERE NOT EXISTS (SELECT 1 FROM tmp_deposit s WHERE s.id = v.id);
DELETE FROM allureport.hakemustunniste h WHERE NOT EXISTS (SELECT 1 FROM tmp_application_tag s WHERE s.id = h.id);
DELETE FROM allureport.hakemus_asiakas a WHERE NOT EXISTS (SELECT 1 FROM tmp_application_customer s WHERE s.id = a.id);
DELETE FROM allureport.hakemuslaji_tarkenne h WHERE NOT EXISTS (SELECT 1 FROM tmp_kind_specifier s WHERE s.id = h.id);
DELETE FROM allureport.hakemuslaji h WHERE NOT EXISTS (SELECT 1 FROM tmp_application_kind s WHERE s.id = h.id);
DELETE FROM allureport.tapahtuma h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.lyhyt_maanvuokraus h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.muistiinpano h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.liikennejarjestely h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.aluevuokraus h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.sijoitussopimus h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.kaivuilmoitus h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.johtoselvitys h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.hakemus_id);
DELETE FROM allureport.hakemus h WHERE NOT EXISTS (SELECT 1 FROM tmp_application s WHERE s.id = h.id);
DELETE FROM allureport.hanke h WHERE NOT EXISTS (SELECT 1 FROM tmp_project s WHERE s.id = h.id);
DELETE FROM allureport.asiakas a WHERE NOT EXISTS (SELECT 1 FROM tmp_customer s WHERE s.id = a.id);
DELETE FROM allureport.kaupunginosa k WHERE NOT EXISTS (SELECT 1 FROM tmp_city_district s WHERE s.id = k.id);
