-- Set existing Kansalaistori sections inactive to allow showing them in ui for existing applications
update fixed_location set is_active = false where section in ('A', 'B', 'C') and area_id = (select id from location_area where name = 'Kansalaistori');

-- Insert new fixed location with section A geometry to cover whole Kansalaistori
insert into fixed_location (area_id, section, application_kind, is_active, geometry)
  (select la.id, null, fl.application_kind, true, fl.geometry from location_area la left join fixed_location fl on la.id = fl.area_id where la.name = 'Kansalaistori' and fl.section = 'A');