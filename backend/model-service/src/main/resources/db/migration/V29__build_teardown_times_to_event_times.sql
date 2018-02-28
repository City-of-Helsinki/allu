do
$$
declare
  r record;
  extension jsonb;
  updatedExtension jsonb;
  application_start int;
  application_end int;
  buildSeconds int;
  teardownSeconds int;
  eventStart double precision;
  eventEnd double precision;
begin
  for r in
  select * from allu.application where type = 'EVENT'
  loop
    extension:=r.extension;
    application_start:=extract(epoch from r.start_time);
    application_end:=extract(epoch from r.end_time);
    buildSeconds:=extension->>'buildSeconds';
    teardownSeconds:=extension->>'teardownSeconds';
    eventStart:=application_start + buildSeconds;
    eventEnd:=application_end - teardownSeconds;

    -- Remove old fields
    updatedExtension:=extension;
    updatedExtension:= updatedExtension - 'buildSeconds';
    updatedExtension:= updatedExtension - 'teardownSeconds';

    -- Add new fields
    updatedExtension:= updatedExtension || jsonb_build_object('eventStartTime', eventStart);
    updatedExtension:= updatedExtension || jsonb_build_object('eventEndTime', eventEnd);

    update application set extension = updatedExtension where id = r.id;
  end loop;
end;
$$
LANGUAGE plpgsql;