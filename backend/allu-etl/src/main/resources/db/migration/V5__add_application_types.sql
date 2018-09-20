create table allureport.johtoselvitys (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  johtokartoitettava boolean,
  tyon_kuvaus text,
  karttaotteiden_maara integer,
  kartta_paivitetty boolean,
  rakentaminen boolean,
  kunnossapito boolean,
  hatatyo boolean,
  tontti_kiinteisto_liitos boolean,
  voimassaoloaika timestamp with time zone
);

create table allureport.kaivuilmoitus (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  pks_kortti boolean,
  rakentaminen boolean,
  kunnossapito boolean,
  hatatyo boolean,
  tontti_kiinteisto_liitos boolean,
  toiminnallinen_kunto timestamp with time zone,
  tyo_valmis timestamp with time zone,
  luvaton_alkuaika timestamp with time zone,
  luvaton_loppuaika timestamp with time zone,
  takuu_paattyy timestamp with time zone,
  asiakas_alkuaika timestamp with time zone,
  asiakas_loppuaika timestamp with time zone,
  asiakas_toiminnallinen_kunto timestamp with time zone,
  asiakas_tyo_valmis timestamp with time zone,
  toiminnallinen_kunto_ilmoitettu timestamp with time zone,
  tyo_valmis_ilmoitettu timestamp with time zone,
  johtoselvitys_id integer references allureport.hakemus(id),
  tyon_tarkoitus text,
  liikennejarjestelyt text,
  liikennehaitta text
);
