alter table allureport.sijainti
  add column asiakas_alkuaika timestamp with time zone,
  add column asiakas_loppuaika timestamp with time zone,
  add column asiakas_voimassaolo_ilmoitettu timestamp with time zone
;
