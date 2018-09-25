delete from allu.fixed_location where area_id in (select id from location_area where name = 'Kolmensepänaukio') and (section = 'J6' or section = 'J7');

update allu.fixed_location set section='J6' where area_id in (select id from location_area where name = 'Kolmensepänaukio') and section='J8';
update allu.fixed_location set section='J7' where area_id in (select id from location_area where name = 'Kolmensepänaukio') and section='J9';
update allu.fixed_location set section='J8' where area_id in (select id from location_area where name = 'Kolmensepänaukio') and section='J10';
update allu.fixed_location set section='J9' where area_id in (select id from location_area where name = 'Kolmensepänaukio') and section='J11';
update allu.fixed_location set section='J10' where area_id in (select id from location_area where name = 'Kolmensepänaukio') and section='J12';
