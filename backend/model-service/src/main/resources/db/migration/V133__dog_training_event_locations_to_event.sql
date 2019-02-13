-- Set existing dog training event sections inactive to allow showing them in ui for existing applications
update fixed_location set is_active = false where application_kind = 'DOG_TRAINING_EVENT';

-- Insert new dog training event sections for event
insert into fixed_location (area_id, section, application_kind, is_active, geometry)
  (select la.id, null, 'OUTDOOREVENT', true, fl.geometry from location_area la left join fixed_location fl on la.id = fl.area_id where fl.application_kind = 'DOG_TRAINING_EVENT');