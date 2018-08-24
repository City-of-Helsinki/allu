update allu.default_text set text_type = 'TERMS' where text_type = 'OTHER' and application_type != 'CABLE_REPORT';

insert into  allu.attribute_meta (name, ui_name, data_type, structure_meta_id)
  values ('TERMS', 'Ehdot', 'ENUM_VALUE', (select id from allu.structure_meta where type_name = 'CableInfoType'));
