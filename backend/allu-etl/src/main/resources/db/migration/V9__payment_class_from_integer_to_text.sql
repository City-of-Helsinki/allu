alter table allureport.sijainti add column maksuluokka_new text;
alter table allureport.sijainti add column syotetty_maksuluokka_new text;

update allureport.sijainti set maksuluokka_new=maksuluokka::text;
update allureport.sijainti set syotetty_maksuluokka_new =syotetty_maksuluokka::text;

alter table allureport.sijainti drop column maksuluokka;
alter table allureport.sijainti drop column syotetty_maksuluokka;

alter table allureport.sijainti rename column maksuluokka_new to maksuluokka;
alter table allureport.sijainti rename column syotetty_maksuluokka_new to syotetty_maksuluokka;
