INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
  VALUES ((select id from allu.structure_meta where type_name='ApplicationKind'),  'MOBILE_SALES', 'Liikkuva myynti/myyntiautot ja -vaunut', 'ENUM_VALUE');
