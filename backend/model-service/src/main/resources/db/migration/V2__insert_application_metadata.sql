------------------------
-- Common metadata
------------------------
-- Contact
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Contact', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Yhteyshenkilön tunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'applicantId', 'Yhteyshenkilön hakijan tunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Yhteyshenkilön nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'streetAddress', 'Katuosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalCode', 'Postinumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'city', 'Kaupunki', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Sähköpostiosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'phoneNumber', 'Puhelin', 'STRING', null, null, null);

-- Postal address
INSERT INTO allu.structure_meta (application_type, version) VALUES ('PostalAddress', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'streetAddress', 'Katuosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalCode', 'Postinumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'city', 'Kaupunki', 'STRING', null, null, null);

-- Applicant
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Applicant', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hakijan tunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Hakijan tyyppi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Hakijan nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalAddress', 'Hakijan osoitetiedot', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'PostalAddress' and version = 1), null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Hakijan sähköpostiosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'phone', 'Hakijan puhelinnumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'registryKey', 'Henkilö-/Y-tunnus', 'STRING', null, null, null);


------------------------
-- Event metadata
------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('EVENT', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
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
    VALUES (currval('allu.structure_meta_id_seq'), 'attendees', 'Yleisömäärä', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'entryFee', 'Osallistumismaksu, jos urheilutapahtuma', 'MONEY', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'ecoCompass', 'Hakijalla Ekokompassi tapahtuma -sertifikaatti (-30 %)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'noPriceReason', 'Peruste korvauksettomuudelle', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'salesActivity', 'Tapahtuma sisältää kaupallista toimintaa (+50%)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'heavyStructure', 'Urheilutapahtuma sisältää raskaita rakenteita tai osallistujille maksullinen (+50 %)', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodSales', 'Tapahtuma sisältää elintarvikemyyntiä tai tarjoilua', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodProviders', 'Elintarviketoimijat', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'marketingProviders', 'Markkinointitoimijat', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureArea', 'Rakenteiden kokonaisneliömäärä', 'FLOAT', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureDescription', 'Rakenteiden kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureStartTime', 'Rakennuspäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureEndTime', 'Purkupäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'timeExceptions', 'Tapahtuma-ajan poikkeukset', 'STRING', null, null, null);

---------------------------
-- ShortTermRental metadata
---------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('SHORT_TERM_RENTAL', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Vuokrauksen kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'commercial', 'Kaupallinen', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'largeSalesArea', 'Suuri myyntialue', 'BOOLEAN', null, null, null);


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
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableSurveyRequired', 'Johtokartoitettava', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableReportId', 'Johtoselvitystunnus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'workDescription', 'Työn kuvaus', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'owner', 'Omistaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'mapExtractCount', 'Karttaotteiden määrä', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'infoEntries', 'Johtotiedot', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where application_type = 'CableInfoEntry' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'mapUpdated', 'Kartta päivitetty', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'constructionWork', 'Rakentaminen', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'maintenanceWork', 'Kunnossapito', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'emergencyWork', 'Hätätyö', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyConnectivity', 'Kiinteistöliitos', 'BOOLEAN', null, null, null);

-----------------------------------
-- Excavation announcement metadata
-----------------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('EXCAVATION_ANNOUNCEMENT', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contractor', 'Työn suorittaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'responsiblePerson', 'Vastuuhenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyDeveloper', 'Rakennuttaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyDeveloperContact', 'Rakennuttajan yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'pksCard', 'PKS-kortti', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'constructionWork', 'Rakentaminen', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'maintenanceWork', 'Kunnossapito', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'emergencyWork', 'Hätätyö', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyConnectivity', 'Kiinteistöliitos', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'winterTimeOperation', 'Talvityön toiminnallinen kunto', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'summerTimeOperation', 'Kesätyön toiminnallinen kunto', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'workFinished', 'Työ valmis', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'unauthorizedWorkStartTime', 'Luvattoman kaivutyön aloitusaika', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'unauthorizedWorkEndTime', 'Luvattoman kaivutyön lopetusaika', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'guaranteeEndTime', 'Takuun päättymispäivämäärä', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'cableReportId', 'Johtoselvitys kaivuilmoitukselle', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'trafficArrangements', 'Suoritettavat liikennejärjestelytyöt', 'STRING', null, null, null);

----------------
-- Note metadata
----------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('NOTE', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'reoccurring', 'Toistuva', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Kuvaus', 'STRING', null, null, null);

------------------------------------------
-- Temporary traffic arrangements metadata
------------------------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('TEMPORARY_TRAFFIC_ARRANGEMENTS', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contractor', 'Työn suorittaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'responsiblePerson', 'Vastuuhenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'pksCard', 'PKS-kortti', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'workFinished', 'Työ valmis', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'trafficArrangements', 'Suoritettavat liikennejärjestelytyöt', 'STRING', null, null, null);

------------------------------------------
-- Placement contract metadata
------------------------------------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('PLACEMENT_CONTRACT', 1);

INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'representative', 'Asiamies', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'diaryNumber', 'Diaarinumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'generalTerms', 'Yleiset ehdot', 'STRING', null, null, null);

----------
-- Project
----------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Project', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hankkeen tunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Hankkeen nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Hankkeen alkuaika', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Hankkeen loppumisaika', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'cityDistricts', 'Kaupunginosat', 'LIST', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'ownerName', 'Hankkeen omistajan nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contactName', 'Hankkeen kontaktin nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Hankkeen sähköpostiosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'phone', 'Hankkeen puhelinnumero', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'customerReference', 'Asiakkaan viite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Hankkeen lisätietoa', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'parentId', 'Hankkeen tunniste', 'INTEGER', null, null, null);

----------
-- User
----------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('User', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Käyttäjän tunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'userName', 'Käyttäjänimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'realName', 'Käyttäjän koko nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'emailAddress', 'Käyttäjän sähköpostiosoite', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'title', 'Titteli', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'isActive', 'Aktiivinen', 'BOOLEAN', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'allowedApplicationTypes', 'Sallitut hakemustyypit', 'LIST', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'assignedRoles', 'Roolit', 'LIST', 'STRING', null, null);

-----------------
-- ApplicationTag
-----------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('ApplicationTag', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'addedBy', 'Lisääjän käyttäjätunniste', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Tunnisteen tyyppi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'creationTime', 'Luontiaika', 'DATETIME', null, null, null);

--------------
-- Application
--------------
INSERT INTO allu.structure_meta (application_type, version) VALUES ('Application', 1);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hakemuksen tunniste tietokannassa', 'INTEGER', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'applicationId', 'Hakemuksen tunniste ihmisille', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'project', 'Hanke, johon hakemus liittyy', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Project' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'handler', 'Hakemuksen käsittelijä', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'User' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'status', 'Hakemuksen tila', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'type', 'Hakemuksen tyyppi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'kind', 'Hakemuksen laji', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'applicationTags', 'Hakemuksen tagit', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where application_type = 'ApplicationTag' and version = 1),
            null);
-- skip 'metadata' (not visible in UI)
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'name', 'Hakemuksen nimi', 'STRING', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'creationTime', 'Hakemuksen luontiaika', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Hakemuksen aktiivisuus alkaa', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Hakemuksen aktiivisuus loppuu', 'DATETIME', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'applicant', 'Hakija', 'STRUCTURE', null,
            (select id from allu.structure_meta where application_type = 'Applicant' and version = 1),
            null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'contactList', 'Hakemuksen yhteyshenkilöt', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where application_type = 'Contact' and version = 1),
            null);
-- TODO: location
-- TODO: extension
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'decisionTime', 'Päätöksen aikaleima', 'DATETIME', null, null, null);
-- TODO: attachmentList
-- TODO: comments
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'calculatedPrice', 'Laskettu hinta', 'MONEY', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'), 'priceOverride', 'Korvaava hinta', 'MONEY', null, null, null);
INSERT INTO allu.attribute_meta (structure, name, ui_name, data_type, list_type, structure_attribute, validation_rule)
    VALUES (currval('allu.structure_meta_id_seq'),  'priceOverrideReason', 'Korvaavan hinnan peruste', 'STRING', null, null, null);

