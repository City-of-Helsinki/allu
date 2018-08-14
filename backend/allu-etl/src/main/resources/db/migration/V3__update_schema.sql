alter table allureport.asiakas 
  add column maa text,
  add column aktiivinen boolean
;

alter table allureport.hanke
  add column asiakas_id integer references allureport.asiakas(id),
  add column tunniste text,
  add column lisaaja text
;

alter table allureport.hakemus 
  add column asiointitunnus text,
  add column asiakasjarjestelma_kayttaja text
;


alter table allureport.lyhyt_maanvuokraus
  rename column iso_myyntialue to laskutettava_myyntialue
;

alter table allureport.hakemuskommentti rename to kommentti;

alter table allureport.kommentti 
  add column kommentoija text,
  add column hanke_id integer references allureport.hanke(id)
;

alter table allureport.muutoshistoria 
  add column hanke_id integer references allureport.hanke(id)
;

alter table allureport.muutoshistoria 
  rename column uusi_tila to muutostarkenne
;

create table allureport.liikennejarjestely (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  tyon_tarkoitus text,
  liikennejarjestelyt text,
  liikennehaitta text
);

create table allureport.sijoitussopimus (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  kiinteistotunnus text,
  tyonkuvaus text,
  sopimusteksti text,
  irtisanomispaiva timestamp with time zone,
  pykala integer
);

create table allureport.sopimus (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  luonti_aika timestamp with time zone, 
  vastausaika timestamp with time zone,
  status text,
  hylkays_syy text,
  allekirjoittaja text,
  puitesopimus boolean,
  sopimus_liitteena boolean
);

create table allureport.taydennyspyynto (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  luonti_aika timestamp with time zone,
  lisaaja text,
  status text
);

create table allureport.taydennyspyynto_kentta (
  id integer primary key,
  taydennyspyynto_id integer references allureport.taydennyspyynto(id),
  kentta text,
  kuvaus text
);

alter table allureport.hakemus rename column luonti_aika to luontiaika;
alter table allureport.hakemustunniste rename column luonti_aika to luontiaika;
alter table allureport.kommentti rename column luonti_aika to luontiaika;
alter table allureport.kommentti rename column paivitys_aika to paivitysaika;
alter table allureport.muutoshistoria rename column muutos_aika to muutosaika;
alter table allureport.sijainti rename column alku_aika to alkuaika;
alter table allureport.sijainti rename column loppu_aika to loppuaika;
alter table allureport.sopimus rename column luonti_aika to luontiaika;
alter table allureport.tapahtuma rename column elintarvike_toimijat to elintarviketoimijat;
alter table allureport.taydennyspyynto rename column luonti_aika to luontiaika;
alter table allureport.vakuus rename column luonti_aika to luontiaika;
alter table allureport.valvontatehtava rename column luonti_aika to luontiaika;
