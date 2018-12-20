create table allureport.laskutusjakso (
  id integer primary key,
  hakemus_id integer references allureport.hakemus(id),
  laskutettu boolean,
  alkuaika timestamp with time zone,
  loppuaika timestamp with time zone
);

alter table allureport.lasku add column laskutusjakso_id integer references allureport.laskutusjakso(id);
alter table allureport.laskuperuste add column laskutusjakso_id integer references allureport.laskutusjakso(id);
