------------------------
-- Common metadata
------------------------
-- Contact
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Contact', 1);
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

-- Applicant
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Applicant', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'personName', 'Henkilön nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'companyName', 'Yrityksen nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'organizationName', 'Yhdistyksen nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'businessId', 'Y-tunnus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'ssn', 'Henkilötunnus', 'STRING', null, null, null);
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


------------------------
-- Event metadata
------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('EVENT', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Tapahtuman nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'nature', 'Tapahtuman luonne', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Tapahtuman kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'url', 'Tapahtuman www-sivu', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'eventStartTime', 'Tapahtuman alkupäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'eventEndTime', 'Tapahtuman loppupäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'timeExceptions', 'Tapahtuma-ajan poikkeukset', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'attendees', 'Yleisömäärä', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'entryFee', 'Osallistumismaksu, jos urheilutapahtuma', 'MONEY', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'noPriceReason', 'Peruste korvauksettomuudelle', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'salesOrPromotionThemeless', 'Tapahtuma sisältää kaupallista toimintaa (+50%)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'heavyStructure', 'Urheilutapahtuma sisältää raskaita rakenteita tai osallistujille maksullinen (+50 %)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'ecoCompass', 'Hakijalla Ekokompassi tapahtuma -sertifikaatti (-30 %)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'salesOrPromotionDescription', 'Myynti- tai mainostoiminnan kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodSales', 'Tapahtuma sisältää elintarvikemyyntiä tai tarjoilua', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodProviders', 'Elintarviketoimijat', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureArea', 'Rakenteiden kokonaisneliömäärä', 'FLOAT', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureDescription', 'Rakenteiden kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureStartTime', 'Rakennuspäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureEndTime', 'Purkupäivämäärä', 'DATETIME', null, null, null);
-- Use common Contact definition
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (
        currval('allu.structure_meta_id_seq'),
        'contact',
        'Yhteyshenkilö',
        'LIST',
        'STRUCTURE',
        (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
        null);
-- Use common Applicant definition
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (
        currval('allu.structure_meta_id_seq'),
        'applicant',
        'Hakija',
        'STRUCTURE',
        null,
        (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
        null);

---------------------------
-- ShortTermRental metadata
---------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('SHORT_TERM_RENTAL', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Vuokrauksen kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'commercial', 'Kaupallinen', 'BOOLEAN', null, null, null);
-- Use common Contact definition
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
VALUES (
    currval('allu.structure_meta_id_seq'),
    'contact',
    'Yhteyshenkilö',
    'LIST',
    'STRUCTURE',
    (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
    null);
-- Use common Applicant definition
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
VALUES (
    currval('allu.structure_meta_id_seq'),
    'applicant',
    'Hakija',
    'STRUCTURE',
    null,
    (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
    null);

------------------------
-- Cable report metadata
------------------------
-- CableInfoEntry
INSERT INTO allu.structure_meta (application_type, version) VALUES ('CableInfoEntry', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Tyyppi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätietoja', 'STRING', null, null, null);

-- CABLE_REPORT
INSERT INTO allu.structure_meta (application_type, version) VALUES ('CABLE_REPORT', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableReportId', 'Johtoselvitystunnus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'workDescription', 'Työn kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'reportStartTime', 'Arvioitu aloitus', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'reportEndTime', 'Arvioitu lopetus', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'applicant', 'Hakija', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'mapExtractCount', 'Karttaotteiden määrä', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableSurveyRequired', 'Johtokartoitettava', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'infoEntries', 'Johtotiedot', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where application_type = 'CableInfoEntry' and version = 1),
            null);



