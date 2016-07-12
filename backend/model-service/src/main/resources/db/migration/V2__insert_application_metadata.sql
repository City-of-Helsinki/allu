------------------------
-- OutdoorEvent metadata
------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('OUTDOOREVENT', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'nature', 'Tapahtuman luonne', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Tapahtuman kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'url', 'Tapahtuman www-sivu', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Alkupäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Loppupäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'attendees', 'Yleisömäärä', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'pricing', 'Osallistumismaksu, jos urheilutapahtuma', 'MONEY', null, null, null);


-- Contacts structure definition (not that currval may refer to different value than above)
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'LIST', 'STRUCTURE', null, null);
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Contact', 1);
UPDATE allu.attribute_meta set structure_attribute = currval('allu.structure_meta_id_seq') where id = currval('allu.attribute_meta_id_seq');
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contactName', 'Yhteyshenkilön nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'address', 'Osoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalCode', 'Postinumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'postOffice', 'Toimipaikka', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'phoneNumber', 'Puhelin', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Sähköpostiosoite', 'STRING', null, null, null);
