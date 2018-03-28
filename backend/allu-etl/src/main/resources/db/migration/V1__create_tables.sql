create table allureport.kaupunginosa (
  id integer primary key,
  tunnus integer not null unique,
  nimi text);

create table allureport.asiakas (
  id integer primary key,
  katuosoite text,
  postinumero text,
  postitoimipaikka text,
  tyyppi text not null,
  nimi text not null,
  tunniste text,
  ovt text,
  operaattoritunnus text,
  email text,
  puhelin text,
  sap_asiakas_numero text,
  laskutuskielto boolean,
  laskutusasiakas boolean);

create table allureport.hanke (
  id integer primary key,
  liittyy_hankkeeseen integer references allureport.hanke(id),
  nimi text,
  alkuaika timestamp with time zone,
  loppuaika timestamp with time zone,
  kaupunginosa_id integer[],
  asiakkaan_viite text,
  lisatiedot text);

create table allureport.hakemus (
  id integer primary key,
  hakemuksen_tunnus text unique not null,
  hanke_id integer references allureport.hanke(id),
  nimi text,
  kasittelija text,
  omistaja text,
  tila text,   
  tyyppi text not null,
  luonti_aika timestamp with time zone,
  alkuaika timestamp with time zone,
  loppuaika timestamp with time zone,
  toistuvuuden_loppuaika timestamp with time zone,
  paatoksen_julkisuus text not null,
  paatosaika timestamp with time zone,
  paatoksen_tekija text,
  laskettu_hinta integer,
  ei_laskutettava boolean not null,
  ei_laskutettava_peruste text,
  laskutusasiakas_id integer references allureport.asiakas(id),
  laskutettu boolean,
  korvaava_hakemus_id integer references allureport.hakemus(id),
  asiakkaan_viite text,
  laskutuspaiva timestamp with time zone);

create table allureport.tapahtuma (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  tapahtuman_luonne text,
  kuvaus text,
  www_sivu text,
  tapahtuman_alkuaika timestamp with time zone,
  tapahtuman_loppuaika timestamp with time zone,
  yleisomaara integer,
  osallistumismaksu integer,
  eko_kompassi boolean,
  elintarvikemyynti boolean,
  elintarvike_toimijat text,
  rakenteiden_neliomaara  double precision,
  rakenteiden_kuvaus text,
  tapahtuma_ajan_poikkeukset text);

create table allureport.lyhyt_maanvuokraus (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  kuvaus text,
  kaupallinen boolean,
  iso_myyntialue boolean);

create table allureport.muistiinpano (
  hakemus_id integer primary key references allureport.hakemus(id),
  ehdot text,
  kuvaus text);

create table allureport.hakemuslaji (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id) not null,
  laji text);

create table allureport.hakemuslaji_tarkenne (
  id integer primary key,
  hakemuslaji_id integer references allureport.hakemuslaji(id)  null,
  tarkenne text);

create table allureport.hakemus_asiakas (
  id integer primary key,
  asiakas_id integer references allureport.asiakas(id) not null,
  hakemus_id integer references allureport.hakemus(id) not null,
  asiakkaan_rooli text not null);

create table allureport.toistuvuusjakso (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id)  not null,
  toistuvuus_alku timestamp with time zone not null,
  toistuvuus_loppu timestamp with time zone not null);

create table allureport.hakemustunniste (
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id),
  lisaaja text,
  tyyppi text not null,
  luonti_aika timestamp with time zone not null);

create table allureport.vakuus (
  id integer primary key,
  hakemus_id integer unique not null references allureport.hakemus(id),
  maara integer not null,
  syy text,
  status text not null,
  lisaaja text,
  luonti_aika timestamp with time zone not null);

create table allureport.alue (
  id integer primary key,
  nimi text not null);

create table allureport.kiinteasijainti (
  id integer primary key,
  alue_id integer not null references allureport.alue(id),
  lohko text,
  hakemuslaji text not null,
  aktiivinen boolean not null,
  geometria geometry(GEOMETRY, 3879));

create table allureport.sijainti(
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id) ,
  sijainti_avain integer not null,
  sijainti_versio integer not null,
  alku_aika timestamp with time zone not null,
  loppu_aika timestamp with time zone not null,
  lisatiedot text,
  katuosoite text,
  postinumero text,
  postitoimipaikka text,
  pinta_ala double precision,
  syotetty_pinta_ala double precision,
  kaupunginosa_id integer references allureport.kaupunginosa(id),
  syotetty_kaupunginosa_id integer references allureport.kaupunginosa(id),
  maksuluokka integer,
  syotetty_maksuluokka integer,
  altakuljettava boolean not null default false);

create table allureport.sijainti_kiinteasijainti (
  id integer primary key,
  sijainti_id integer references allureport.sijainti(id),
  kiinteasijainti_id integer references allureport.kiinteasijainti(id) );

create table allureport.sijainti_geometria (
  id integer primary key,
  geometria geometry(GEOMETRY, 3879),
  sijainti_id integer references allureport.sijainti(id));

create table allureport.liite (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  tyyppi text not null,
  nimi text,
  kuvaus text,
  paatoksen_liite boolean,
  koko bigint,
  luonti_aika timestamp with time zone,
  mime_tyyppi text,
  data bytea);

create table allureport.laskuperuste (
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id),
  tunniste text,
  viitattu_tunniste text,
  manuaalisesti_asetettu boolean not null,
  tyyppi text not null,
  yksikko text not null,
  maara double precision not null,
  teksti text not null,
  perusteet text[],
  yksikkohinta integer not null,
  kokonaishinta integer not null);

create table allureport.paatos (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  luonti_aika timestamp with time zone,
  data bytea);

create table allureport.hakemuskommentti (
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id),
  lisaaja text,
  tyyppi text not null,
  teksti text not null,
  luonti_aika timestamp with time zone not null,
  paivitys_aika timestamp with time zone not null);

create table allureport.muutoshistoria (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  asiakas_id integer references allureport.asiakas(id),
  kayttaja text,
  muutostyyppi text not null,
  uusi_tila text,
  muutos_aika timestamp with time zone not null);

create table allureport.kenttamuutos (
  id integer primary key,
  muutoshistoria_id integer not null references allureport.muutoshistoria(id),
  kentan_nimi text not null,
  vanha_arvo text,
  uusi_arvo text);

create table allureport.valvontatehtava (
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id),
  tyyppi text not null,
  lisaaja text,
  omistaja text,
  luonti_aika timestamp with time zone not null,
  suunniteltu_loppuaika timestamp with time zone not null,
  toteutunut_loppuaika timestamp with time zone,
  status text not null,
  kuvaus text,
  tulos text);

create table allureport.lasku (
  id integer primary key,
  hakemus_id integer not null references allureport.hakemus(id),
  asiakas_tyyppi text,
  asiakas_nimi text,
  asiakas_ovt text,
  asiakas_tunniste text,
  asiakas_email text,
  asiakas_puhelin text,
  asiakas_katuosoite text,
  asiakas_postinumero text,
  asiakas_postitoimipaikka text,
  laskutettava_aika timestamp with time zone not null,
  laskutettu boolean not null default false,
  sap_tunnus_puuttuu boolean not null default false);

create table allureport.laskurivi (
  id integer primary key,
  lasku_id integer not null references allureport.lasku(id),
  rivinumero integer not null,
  yksikko text not null,
  maara double precision not null,
  teksti text not null,
  perusteet text[],
  yksikkohinta integer not null,
  kokonaishinta integer not null);
