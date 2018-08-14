WITH hakemuslajit (en, fi) AS (
    VALUES
        ('OUTDOOREVENT', 'Ulkoilmatapahtuma'),
        ('PROMOTION', 'Promootio'),
        ('AREA_RENTAL', 'Aluevuokraus'),
        ('TEMPORARY_TRAFFIC_ARRANGEMENTS', 'Tilapäiset liikennejärjestelyt'),
        ('BRIDGE_BANNER', 'Banderollit silloissa'),
        ('BENJI', 'Benji-hyppylaite'),
        ('PROMOTION_OR_SALES', 'Esittely- tai myyntitila liikkeen edustalla'),
        ('URBAN_FARMING', 'Kaupunkiviljelypaikka'),
        ('KESKUSKATU_SALES', 'Keskuskadun myyntipaikka'),
        ('SUMMER_THEATER', 'Kesäteatterit'),
        ('DOG_TRAINING_FIELD', 'Koirakoulutuskentät'),
        ('DOG_TRAINING_EVENT', 'Koirakoulutustapahtuma'),
        ('CARGO_CONTAINER', 'Kontti'),
        ('SMALL_ART_AND_CULTURE', 'Pienimuotoinen taide- ja kulttuuritoiminta'),
        ('SEASON_SALE', 'Sesonkimyynti'),
        ('CIRCUS', 'Sirkus/tivolivierailu'),
        ('ART', 'Taideteos'),
        ('STORAGE_AREA', 'Varastoalue'),
        ('STREET_AND_GREEN', 'Katu- ja vihertyöt'),
        ('WATER_AND_SEWAGE', 'Vesi / viemäri'),
        ('ELECTRICITY', 'Sähkö'),
        ('DATA_TRANSFER', 'Tiedonsiirto'),
        ('HEATING_COOLING', 'Lämmitys/viilennys'),
        ('CONSTRUCTION', 'Rakennus'),
        ('YARD', 'Piha'),
        ('GEOLOGICAL_SURVEY', 'Pohjatutkimus'),
        ('PROPERTY_RENOVATION', 'Kiinteistöremontti'),
        ('CONTAINER_BARRACK', 'Kontti/parakki'),
        ('PHOTO_SHOOTING', 'Kuvaus'),
        ('SNOW_WORK', 'Lumenpudotus'),
        ('RELOCATION', 'Muutto'),
        ('LIFTING', 'Nostotyö'),
        ('NEW_BUILDING_CONSTRUCTION', 'Työmaa-alue'),
        ('ROLL_OFF', 'Vaihtolava'),
        ('CHRISTMAS_TREE_SALES_AREA', 'Joulukuusenmyyntipaikka'),
        ('CITY_CYCLING_AREA', 'Kaupunkipyöräpaikka'),
        ('AGILE_KIOSK_AREA', 'Ketterien kioskien myyntipaikka'),
        ('STATEMENT', 'Lausunto'),
        ('SNOW_HEAP_AREA', 'Lumenkasauspaikka'),
        ('SNOW_GATHER_AREA', 'Lumenvastaanottopaikka'),
        ('OTHER_SUBVISION_OF_STATE_AREA', 'Muun hallintokunnan alue'),
        ('MILITARY_EXCERCISE', 'Sotaharjoitus'),
        ('WINTER_PARKING', 'Talvipysäköinti'),
        ('REPAVING', 'Uudelleenpäällystykset'),
        ('ELECTION_ADD_STAND', 'Vaalimainosteline'),
        ('PUBLIC_EVENT', 'Yleisötilaisuus'),
        ('OTHER', 'Muu'))
INSERT INTO allureport.kiinteasijainti (
  id,
  alue_id,
  lohko,
  hakemuslaji,
  aktiivinen,
  geometria
)
SELECT
    f.id AS id,
    f.area_id AS alue_id,
    f.section AS lohko,
    l.fi AS hakemuslaji,
    f.is_active AS aktiivinen,
    f.geometry AS geometria
FROM allu_operative.fixed_location f
LEFT JOIN hakemuslajit l ON f.application_kind = l.en
ON CONFLICT (id) DO UPDATE SET
    alue_id = EXCLUDED.alue_id,
    lohko = EXCLUDED.lohko,
    hakemuslaji = EXCLUDED.hakemuslaji,
    geometria = EXCLUDED.geometria
;

DELETE FROM allureport.kiinteasijainti k WHERE NOT EXISTS (SELECT id FROM allu_operative.fixed_location of WHERE of.id = k.id);
