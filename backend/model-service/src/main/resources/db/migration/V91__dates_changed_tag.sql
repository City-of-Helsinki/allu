insert into allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
VALUES ((select id from allu.structure_meta where type_name='ApplicationTagType'),
        'DATE_CHANGE', 'Aikamuutos', 'ENUM_VALUE');

insert into allu.attribute_meta (structure_meta_id,name,ui_name,data_type) values (
  (select id from allu.structure_meta where type_name='EXCAVATION_ANNOUNCEMENT'), 'validityReported', 'Voimassaolo ilmoitettu', 'DATETIME');

