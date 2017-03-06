--------------------
-- Enumeration types
--------------------
-- ApplicantType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicantType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PERSON', 'Yksityishenkilö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'COMPANY', 'Yritys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ASSOCIATION', 'Yhdistys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PROPERTY', 'Kiinteistö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER', 'Muu', 'ENUM_VALUE');

-- ApplicationKind
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationKind', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PROMOTION', 'Promootio', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OUTDOOREVENT', 'Ulkoilmatapahtuma', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'BRIDGE_BANNER', 'Banderollit silloissa', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'BENJI', 'Benji-hyppylaite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PROMOTION_OR_SALES', 'Esittely- tai myyntitila liikkeen edustalla', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'URBAN_FARMING', 'Kaupunkiviljelypaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'KESKUSKATU_SALES', 'Keskuskadun myyntipaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SUMMER_THEATER', 'Kesäteatterit', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DOG_TRAINING_FIELD', 'Koirakoulutuskentät', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DOG_TRAINING_EVENT', 'Koirakoulutustapahtuma', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER_SHORT_TERM_RENTAL', 'Muu lyhytaikainen maanvuokraus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SMALL_ART_AND_CULTURE', 'Pienimuotoinen taide- ja kulttuuritoiminta', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SEASON_SALE', 'Sesonkimyynti', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CIRCUS', 'Sirkus/tivolivierailu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ART', 'Taideteos', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STORAGE_AREA', 'Varastoalue', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STREET_AND_GREEN', 'Katu- ja vihertyöt', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WATER_AND_SEWAGE', 'Vesi / viemäri', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ELECTRICITY', 'Sähkö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DATA_TRANSFER', 'Tiedonsiirto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'HEATING_COOLING', 'Lämmitys/viilennys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CONSTRUCTION', 'Rakennus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'YARD', 'Piha', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GEOLOGICAL_SURVEY', 'Pohjatutkimus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER_CABLE_REPORT', 'Muut', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CHRISTMAS_TREE_SALES_AREA', 'Joulukuusenmyyntipaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CITY_CYCLING_AREA', 'Kaupunkipyöräpaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'AGILE_KIOSK_AREA', 'Ketterien kioskien myyntipaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STATEMENT', 'Lausunto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SNOW_HEAP_AREA', 'Lumenkasauspaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SNOW_GATHER_AREA', 'Lumenvastaanottopaikka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER_SUBVISION_OF_STATE_AREA', 'Muun hallintokunnan alue', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'MILITARY_EXCERCISE', 'Sotaharjoitus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WINTER_PARKING', 'Talvipysäköinti', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'REPAVING', 'Uudelleenpäällystykset', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ELECTION_ADD_STAND', 'Vaalimainosteline', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'NOTE_OTHER', 'Muu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PUBLIC_EVENT', 'Yleisötilaisuus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER_TEMPORARY_TRAFFIC_ARRANGEMENT', 'Muu', 'ENUM_VALUE');

-- ApplicationSpecifier
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationSpecifier', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ASPHALT', 'Asfaltointityö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'INDUCTION_LOOP', 'Induktiosilmukka', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'COVER_STRUCTURE', 'Kansisto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STREET_OR_PARK', 'Katu tai puisto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PAVEMENT', 'Kiveystyö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TRAFFIC_LIGHT', 'Liikennevalo', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'COMMERCIAL_DEVICE', 'Mainoslaite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TRAFFIC_STOP', 'Pysäkkikatos', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'BRIDGE', 'Silta', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OUTDOOR_LIGHTING', 'Ulkovalaistus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STORM_DRAIN', 'Hulevesi', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WELL', 'Kaivo', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'UNDERGROUND_DRAIN', 'Salaoja', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WATER_PIPE', 'Vesijohto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DRAIN', 'Viemäri', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DISTRIBUTION_CABINET', 'Jakokaappi', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ELECTRICITY_CABLE', 'Kaapeli', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ELECTRICITY_WELL', 'Kaivo', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DISTRIBUTION_CABINET_OR_PILAR', 'Jakokaappi/-pilari', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DATA_CABLE', 'Kaapeli', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DATA_WELL', 'Kaivo', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STREET_HEATING', 'Katulämmitys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DISTRICT_HEATING', 'Kaukolämpö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DISTRICT_COOLING', 'Kaukokylmä', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GROUND_ROCK_ANCHOR', 'Maa- / kallioankkuri', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'UNDERGROUND_STRUCTURE', 'Maanalainen rakenne', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'UNDERGROUND_SPACE', 'Maanalainen tila', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'BASE_STRUCTURES', 'Perusrakenteet', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DRILL_PILE', 'Porapaalu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CONSTRUCTION_EQUIPMENT', 'Rakennuksen laite/varuste', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CONSTRUCTION_PART', 'Rakennuksen osa', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GROUND_FROST_INSULATION', 'Routaeriste', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SMOKE_HATCH_OR_PIPE', 'Savunpoistoluukku/-putki, IV-putki', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STOP_OR_TRANSITION_SLAB', 'Sulku-/siirtymälaatta', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SUPPORTING_WALL_OR_PILE', 'Tukiseinä/-paalu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'FENCE_OR_WALL', 'Aita, muuri, penger', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DRIVEWAY', 'Kulkutie', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STAIRS_RAMP', 'Portaat, luiska tms.', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SUPPORTING_WALL_OR_BANK', 'Tukimuuri/-penger, lujitemaamuuri', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DRILLING', 'Kairaus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TEST_HOLE', 'Koekuoppa', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GROUND_WATER_PIPE', 'Pohjavesiputki', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ABSORBING_SEWAGE_SYSTEM', 'Imujätejärjestelmä', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GAS_PIPE', 'Kaasujohto', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER', 'Muu', 'ENUM_VALUE');

-- ApplicationTagType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationTagType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ADDITIONAL_INFORMATION_REQUESTED', 'Täydennyspyyntö lähetetty', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STATEMENT_REQUESTED', 'Lausunnolla', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DEPOSIT_REQUESTED', 'Vakuus määritetty', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DEPOSIT_PAID', 'Vakuus suoritettu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PRELIMINARY_INSPECTION_REQUESTED', 'Aloituskatselmuspyyntö lähetetty', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PRELIMINARY_INSPECTION_DONE', 'Aloituskatselmus suoritettu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'FINAL_INSPECTION_AGREED', 'Loppukatselmus sovittu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'FINAL_INSPECTION_DONE', 'Loppukatselmus suoritettu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WAITING', 'Odottaa lisätietoa', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'COMPENSATION_CLARIFICATION', 'Hyvitysselvitys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PAYMENT_BASIS_CORRECTION', 'Maksuperusteet korjattava', 'ENUM_VALUE');

-- ApplicationType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'EXCAVATION_ANNOUNCEMENT', 'Kaivuilmoitus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'AREA_RENTAL', 'Aluevuokraus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TEMPORARY_TRAFFIC_ARRANGEMENTS', 'Tilapäinen liikennejärjestely', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CABLE_REPORT', 'Johtoselvitys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PLACEMENT_CONTRACT', 'Sijoitussopimus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'EVENT', 'Tapahtuma', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SHORT_TERM_RENTAL', 'Lyhytaikainen maanvuokraus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'NOTE', 'Muistiinpano', 'ENUM_VALUE');

-- AttachmentType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('AttachmentType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ADDED_BY_CUSTOMER', 'Asiakkaan lisäämä liite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ADDED_BY_HANDLER', 'Käsittelijän lisäämä liite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DEFAULT', 'Hakemustyyppikohtainen vakioliite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DEFAULT_IMAGE', 'Hakemustyyppikohtainen tyyppikuvaliite', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DEFAULT_TERMS', 'Hakemustyyppikohtainen ehtoliite', 'ENUM_VALUE');

-- CableInfoType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('CableInfoType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TELECOMMUNICATION', 'Tietoliikenne', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ELECTRICITY', 'Sähkö', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'WATER_AND_SEWAGE', 'Vesi ja viemäri', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DISTRICT_HEATING_COOLING', 'Kaukolämpö/jäähdytys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GAS', 'Kaasu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'UNDERGROUND_STRUCTURE', 'Maanalainen rakenne/tila', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'TRAMWAY', 'Raitiotie', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STREET_HEATING', 'Katulämmitys', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'SEWAGE_PIPE', 'Jäteputki', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GEOTHERMAL_WELL', 'Maalämpökaivo', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'GEOTECHNICAL_OBSERVATION_POST', 'Geotekninen tarkkailupiste', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'OTHER', 'Yleisesti/muut', 'ENUM_VALUE');

-- ChangeType !!!!
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ChangeType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CREATED', 'Luotu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'STATUS_CHANGED', 'Siirretty tilaan', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CONTENTS_CHANGED', 'Tietoja päivitetty', 'ENUM_VALUE');

-- CommentType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('CommentType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'INVOICING', 'Laskutuksen kommentti', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'RETURN', 'Valmisteluun palauttajan kommentti', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'REJECT', 'Hylkääjän kommentti', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'INTERNAL', 'Sisäinen kommentti', 'ENUM_VALUE');

-- EventNature
INSERT INTO allu.structure_meta (type_name, version) VALUES ('EventNature', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PUBLIC_FREE', 'Avoin', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PUBLIC_NONFREE', 'Maksullinen', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CLOSED', 'Suljettu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PROMOTION', 'Promootio', 'ENUM_VALUE');

-- RoleType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('RoleType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_CREATE_APPLICATION', 'Hakemuksen luominen', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_PROCESS_APPLICATION', 'Hakemuksen käsittely', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_DECISION', 'Päätöksen teko', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_SUPERVISE', 'Valvonta', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_INVOICING', 'Laskutus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_VIEW', 'Katselu', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'ROLE_ADMIN', 'Ylläpito', 'ENUM_VALUE');

-- StatusType
INSERT INTO allu.structure_meta (type_name, version) VALUES ('StatusType', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PRE_RESERVED', 'Alustava varaus', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'PENDING', 'Hakemus saapunut', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'HANDLING', 'Käsittelyssä', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'RETURNED_TO_PREPARATION', 'Palautettu käsittelyyn', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DECISIONMAKING', 'Odottaa päätöstä', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'DECISION', 'Päätetty', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'REJECTED', 'Hylätty päätös', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'FINISHED', 'Valmis', 'ENUM_VALUE');
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type)
    VALUES (currval('allu.structure_meta_id_seq'),  'CANCELLED', 'Peruttu', 'ENUM_VALUE');

------------------------
-- Common metadata
------------------------

-- Contact
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Contact', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Yhteyshenkilön tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'applicantId', 'Yhteyshenkilön hakijan tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Yhteyshenkilön nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'streetAddress', 'Katuosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalCode', 'Postinumero', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'city', 'Kaupunki', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Sähköpostiosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'phoneNumber', 'Puhelin', 'STRING', null, null);

-- Postal address
INSERT INTO allu.structure_meta (type_name, version) VALUES ('PostalAddress', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'streetAddress', 'Katuosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalCode', 'Postinumero', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'city', 'Kaupunki', 'STRING', null, null);

-- Applicant
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Applicant', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hakijan tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Hakijan tyyppi', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'ApplicantType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Hakijan nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalAddress', 'Hakijan osoitetiedot', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'PostalAddress' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Hakijan sähköpostiosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'phone', 'Hakijan puhelinnumero', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'registryKey', 'Henkilö-/Y-tunnus', 'STRING', null, null);


------------------------
-- Event metadata
------------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('EVENT', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'nature', 'Tapahtuman luonne', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'EventNature' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Tapahtuman kuvaus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'url', 'Tapahtuman www-sivu', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'eventStartTime', 'Tapahtuman alkupäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'eventEndTime', 'Tapahtuman loppupäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'attendees', 'Yleisömäärä', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'entryFee', 'Osallistumismaksu, jos urheilutapahtuma', 'MONEY', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'ecoCompass', 'Hakijalla Ekokompassi tapahtuma -sertifikaatti (-30 %)', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'noPriceReason', 'Peruste korvauksettomuudelle', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'salesActivity', 'Tapahtuma sisältää kaupallista toimintaa (+50%)', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'heavyStructure', 'Urheilutapahtuma sisältää raskaita rakenteita tai osallistujille maksullinen (+50 %)', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodSales', 'Tapahtuma sisältää elintarvikemyyntiä tai tarjoilua', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'foodProviders', 'Elintarviketoimijat', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'marketingProviders', 'Markkinointitoimijat', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureArea', 'Rakenteiden kokonaisneliömäärä', 'FLOAT', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureDescription', 'Rakenteiden kuvaus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureStartTime', 'Rakennuspäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'structureEndTime', 'Purkupäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'timeExceptions', 'Tapahtuma-ajan poikkeukset', 'STRING', null, null);

---------------------------
-- ShortTermRental metadata
---------------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('SHORT_TERM_RENTAL', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Vuokrauksen kuvaus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'commercial', 'Kaupallinen', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'largeSalesArea', 'Suuri myyntialue', 'BOOLEAN', null, null);


------------------------
-- Cable report metadata
------------------------
-- CableInfoEntry
INSERT INTO allu.structure_meta (type_name, version) VALUES ('CableInfoEntry', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Tyyppi', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'CableInfoType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätietoja', 'STRING', null, null);

-- CABLE_REPORT
INSERT INTO allu.structure_meta (type_name, version) VALUES ('CABLE_REPORT', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableSurveyRequired', 'Johtokartoitettava', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'cableReportId', 'Johtoselvitystunnus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'workDescription', 'Työn kuvaus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'owner', 'Omistaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'mapExtractCount', 'Karttaotteiden määrä', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'infoEntries', 'Johtotiedot', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'CableInfoEntry' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'mapUpdated', 'Kartta päivitetty', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'constructionWork', 'Rakentaminen', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'maintenanceWork', 'Kunnossapito', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'emergencyWork', 'Hätätyö', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyConnectivity', 'Kiinteistöliitos', 'BOOLEAN', null, null);

-----------------------------------
-- Excavation announcement metadata
-----------------------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('EXCAVATION_ANNOUNCEMENT', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contractor', 'Työn suorittaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'responsiblePerson', 'Vastuuhenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyDeveloper', 'Rakennuttaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyDeveloperContact', 'Rakennuttajan yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'pksCard', 'PKS-kortti', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'constructionWork', 'Rakentaminen', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'maintenanceWork', 'Kunnossapito', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'emergencyWork', 'Hätätyö', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'propertyConnectivity', 'Kiinteistöliitos', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'winterTimeOperation', 'Talvityön toiminnallinen kunto', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'summerTimeOperation', 'Kesätyön toiminnallinen kunto', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'workFinished', 'Työ valmis', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'unauthorizedWorkStartTime', 'Luvattoman kaivutyön aloitusaika', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'unauthorizedWorkEndTime', 'Luvattoman kaivutyön lopetusaika', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'guaranteeEndTime', 'Takuun päättymispäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'cableReportId', 'Johtoselvitys kaivuilmoitukselle', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'trafficArrangements', 'Suoritettavat liikennejärjestelytyöt', 'STRING', null, null);

----------------
-- Note metadata
----------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('NOTE', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'reoccurring', 'Toistuva', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'description', 'Kuvaus', 'STRING', null, null);

------------------------------------------
-- Temporary traffic arrangements metadata
------------------------------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('TEMPORARY_TRAFFIC_ARRANGEMENTS', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contractor', 'Työn suorittaja', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'responsiblePerson', 'Vastuuhenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'pksCard', 'PKS-kortti', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'workFinished', 'Työ valmis', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'trafficArrangements', 'Suoritettavat liikennejärjestelytyöt', 'STRING', null, null);

------------------------------------------
-- Placement contract metadata
------------------------------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('PLACEMENT_CONTRACT', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'representative', 'Asiamies', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contact', 'Yhteyshenkilö', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'diaryNumber', 'Diaarinumero', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Lisätiedot', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'generalTerms', 'Yleiset ehdot', 'STRING', null, null);

----------
-- Project
----------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Project', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hankkeen tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'name', 'Hankkeen nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Hankkeen alkuaika', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Hankkeen loppumisaika', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'cityDistricts', 'Kaupunginosat', 'LIST', 'INTEGER', null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'ownerName', 'Hankkeen omistajan nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contactName', 'Hankkeen kontaktin nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'email', 'Hankkeen sähköpostiosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'phone', 'Hankkeen puhelinnumero', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'customerReference', 'Asiakkaan viite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'additionalInfo', 'Hankkeen lisätietoa', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'parentId', 'Hankkeen tunniste', 'INTEGER', null, null);

----------
-- User
----------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('User', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Käyttäjän tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'userName', 'Käyttäjänimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'realName', 'Käyttäjän koko nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'emailAddress', 'Käyttäjän sähköpostiosoite', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'title', 'Titteli', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'isActive', 'Aktiivinen', 'BOOLEAN', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'allowedApplicationTypes', 'Sallitut hakemustyypit', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'assignedRoles', 'Roolit', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'RoleType' and version = 1));

-----------------
-- ApplicationTag
-----------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationTag', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'addedBy', 'Lisääjän käyttäjätunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'type', 'Tunnisteen tyyppi', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'ApplicationTagType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'creationTime', 'Luontiaika', 'DATETIME', null, null);

-----------------------
-- ApplicationExtension
-----------------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('ApplicationExtension', 1);

INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'specifiers', 'Tarkenteet', 'LIST', 'ENUMERATION',
            (select id from allu.structure_meta where type_name = 'ApplicationSpecifier' and version = 1));

-----------
-- Location
-----------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Location', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Sijainnin tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'locationKey', 'Sijainnin alue', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'locationVersion', 'Sijainnin alueen versio', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Alkupäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Loppupäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'geometry', 'Alueen geometria', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'area', 'Alueen pinta-ala', 'FLOAT', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'areaOverride', 'Käsittelijän syöttämä pinta-ala', 'FLOAT', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'postalAddress', 'Alueen osoitetiedot', 'STRUCTURE', null,
        (select id from allu.structure_meta where type_name = 'PostalAddress' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'fixedLocationIds', 'Kiinteät alueet', 'LIST', 'INTEGER', null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'cityDistrictId', 'Kaupunginosa', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'cityDistrictOverride', 'Käsittelijän valitsema kaupunginosa', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'paymentTariff', 'Maksuluokka', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'paymentTariffOverride', 'Käsittelijän valitseman maksuluokka', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'underpass', 'Altakuljettava', 'BOOLEAN', null, null);

-------------
-- Attachment
-------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Attachment', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Liitteen tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'handlerName', 'Käsittelijän nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'type', 'Liitteen tyyppi', 'ENUMERATION', null,
        (select id from allu.structure_meta where type_name = 'AttachmentType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'name', 'Liitteen nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'description', 'Liitteen kuvaus', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'size', 'Liitteen koko', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'creationTime', 'Luontipäivämäärä', 'DATETIME', null, null);

-----------
-- Comments
-----------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Comment', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Kommentin tunniste', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'type', 'Kommentin tyyppi', 'ENUMERATION', null,
        (select id from allu.structure_meta where type_name = 'CommentType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'text', 'Kommentti', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'createTime', 'Luontipäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'updateTime', 'Luontipäivämäärä', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'user', 'Kommentoija', 'STRUCTURE', null,
        (select id from allu.structure_meta where type_name = 'User' and version = 1));

--------------
-- Application
--------------
INSERT INTO allu.structure_meta (type_name, version) VALUES ('Application', 1);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'id', 'Hakemuksen tunniste tietokannassa', 'INTEGER', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'applicationId', 'Hakemuksen tunniste ihmisille', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'project', 'Hanke, johon hakemus liittyy', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Project' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'handler', 'Hakemuksen käsittelijä', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'User' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'status', 'Hakemuksen tila', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'StatusType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'type', 'Hakemuksen tyyppi', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'ApplicationType' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'kind', 'Hakemuksen laji', 'ENUMERATION', null,
            (select id from allu.structure_meta where type_name = 'ApplicationKind' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'applicationTags', 'Hakemuksen tagit', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'ApplicationTag' and version = 1));
-- skip 'metadata' (not visible in UI)
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'name', 'Hakemuksen nimi', 'STRING', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'creationTime', 'Hakemuksen luontiaika', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'startTime', 'Hakemuksen aktiivisuus alkaa', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'endTime', 'Hakemuksen aktiivisuus loppuu', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'applicant', 'Hakija', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'Applicant' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'contactList', 'Hakemuksen yhteyshenkilöt', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'Contact' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'locations', 'Hakemuksen sijainti', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'Location' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'extension', 'Hakemuksen laajenne', 'STRUCTURE', null,
            (select id from allu.structure_meta where type_name = 'ApplicationExtension' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'decisionTime', 'Päätöksen aikaleima', 'DATETIME', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'attachmentList', 'Hakemuksen liitteet', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'Attachment' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'comments', 'Hakemuksen kommentit', 'LIST', 'STRUCTURE',
            (select id from allu.structure_meta where type_name = 'Comment' and version = 1));
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'calculatedPrice', 'Laskettu hinta', 'MONEY', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'), 'priceOverride', 'Korvaava hinta', 'MONEY', null, null);
INSERT INTO allu.attribute_meta (structure_meta_id, name, ui_name, data_type, list_type, structure_attribute)
    VALUES (currval('allu.structure_meta_id_seq'),  'priceOverrideReason', 'Korvaavan hinnan peruste', 'STRING', null, null);
