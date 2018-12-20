create table allureport.aluevuokraus (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  pks_kortti boolean,
  haittaa_aiheuttava boolean,
  tyon_tarkoitus text,
  lisatiedot text,
  liikennejarjestelyt text,
  tyo_valmis timestamp with time zone,
  asiakas_tyo_valmis timestamp with time zone,
  tyo_valmis_ilmoitettu timestamp with time zone,
  liikennehaitta text
);
