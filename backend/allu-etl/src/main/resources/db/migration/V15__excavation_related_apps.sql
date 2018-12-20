alter table allureport.kaivuilmoitus
  add column johtoselvitykset text,
  add column sijoitussopimukset text,
  drop column johtoselvitys_id
;
