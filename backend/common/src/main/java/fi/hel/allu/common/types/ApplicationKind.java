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
  STORAGE_AREA, // Varastoalue
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
  NOTE_OTHER // Muu
}
