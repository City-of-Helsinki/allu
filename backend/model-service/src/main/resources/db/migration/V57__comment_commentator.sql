alter table allu.comment add column commentator text;
update allu.comment c set commentator = (select u.user_name from allu.user u where u.id = c.user_id);

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    values ((select id from allu.structure_meta where type_name='CommentType'),  'EXTERNAL_SYSTEM', 'ulkoinen järjestelmä', 'ENUM_VALUE');

insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  values ((select id from allu.structure_meta where type_name = 'Comment'), 'commentator', 'Kommentoijan nimi', 'STRING');