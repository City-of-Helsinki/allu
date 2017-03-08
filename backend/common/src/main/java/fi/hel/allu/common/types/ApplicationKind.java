package fi.hel.allu.common.types;

/**
 * Application kind specifies the subtype of application.
 */
public enum ApplicationKind {
  // for EVENT:
  PROMOTION,
  OUTDOOREVENT,
  // for SHORT_TERM_RENTAL: lyhytaikainen maanvuokraus
  BRIDGE_BANNER, // Banderollit silloissa
  BENJI, // Benji-hyppylaite
  PROMOTION_OR_SALES, // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING, // Kaupunkiviljelypaikka
  KESKUSKATU_SALES, // Keskuskadun myyntipaikka
  SUMMER_THEATER, // Kesäteatterit
  DOG_TRAINING_FIELD, // Koirakoulutuskentät
  DOG_TRAINING_EVENT, // Koirakoulutustapahtuma
  OTHER_SHORT_TERM_RENTAL, // Muu lyhytaikainen maanvuokraus
  SMALL_ART_AND_CULTURE, // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE, // Sesonkimyynti
  CIRCUS, // Sirkus/tivolivierailu
  ART, // Taideteos
  STORAGE_AREA, // Varastoalue, also used by AREA RENTAL!
  // cable reports - johtoselvitykset
  STREET_AND_GREEN, // Katu- ja vihertyöt
  WATER_AND_SEWAGE, // Vesi / viemäri
  ELECTRICITY, // Sähkö
  DATA_TRANSFER, // Tiedonsiirto
  HEATING_COOLING, // Lämmitys/viilennys
  CONSTRUCTION, // Rakennus
  YARD, // Piha
  GEOLOGICAL_SURVEY, // Pohjatutkimus
  OTHER_CABLE_REPORT, // Muut
  // AREA RENTAL
  PROPERTY_RENOVATION,        // kiinteistöremontti
  CONTAINER_BARRACK,          // kontti/parakki
  PHOTO_SHOOTING,             // kuvaus
  SNOW_WORK,                  // lumenpudotus
  RELOCATION,                 // muutto
  LIFTING,                    // nostotyö
  NEW_BUILDING_CONSTRUCTION,  // uudisrakennuksen työmaa-alue
  ROLL_OFF,                   // vaihtolava
  OTHER_AREA_RENTAL,          // muu
  // NOTES
  CHRISTMAS_TREE_SALES_AREA, // Joulukuusenmyyntipaikka
  CITY_CYCLING_AREA, // Kaupunkipyöräpaikka
  AGILE_KIOSK_AREA, // Ketterien kioskien myyntipaikka
  STATEMENT, // Lausunto
  SNOW_HEAP_AREA, // Lumenkasauspaikka
  SNOW_GATHER_AREA, // Lumenvastaanottopaikka
  OTHER_SUBVISION_OF_STATE_AREA, // Muun hallintokunnan alue
  MILITARY_EXCERCISE, // Sotaharjoitus
  WINTER_PARKING, // Talvipysäköinti
  REPAVING, // Uudelleenpäällystykset
  ELECTION_ADD_STAND, // Vaalimainosteline
  NOTE_OTHER, // Muu
  // TEMPORARY TRAFFIC ARRANGEMENTS
  PUBLIC_EVENT, // Yleisötilaisus, also used by AREA RENTAL!
  OTHER_TEMPORARY_TRAFFIC_ARRANGEMENT // Muu
}
