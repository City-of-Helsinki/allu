-- Create new table for attachment data
create table allu.attachment_data (
   id serial primary key,
   size bigint,
   data bytea
);

-- Create reference from attachment to attachment data
alter table allu.attachment add column attachment_data_id integer references allu.attachment_data(id);

-- Copy data from allu.attachment to new attachment_data table
-- and set id of created attachment_data row to allu.attachment.
do
$$
declare
    attm allu.attachment%rowtype;
    data_id integer;
begin
    for attm in
        select * from allu.attachment
    loop
        data_id:= nextval(pg_get_serial_sequence('allu.attachment_data', 'id'));
        insert into allu.attachment_data values (data_id, attm.size, attm.data);
        update allu.attachment set attachment_data_id = data_id where id = attm.id ;
    end loop;
end;
$$
LANGUAGE plpgsql;

-- Drop data and size columns from attachment table
alter table allu.attachment drop column data, drop column size;

INSERT INTO allu.structure_meta (type_name, version) VALUES ('AttachmentData', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'id', 'Liitedatan tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'size', 'Liitdatan koko', 'INTEGER', null, null);


INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES ((select id from allu.structure_meta where type_name = 'Attachment'),  'attachmentDataId', 'Liitteen data', 'STRUCTURE', null, 
      (select id from allu.structure_meta where type_name = 'AttachmentData' and version = 1));
