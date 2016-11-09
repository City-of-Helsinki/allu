package fi.hel.allu.common.types;

/**
 * Application categories define grouping for different application types. Each of the groups is mapped to same set of data i.e. although
 * application type is different, the data content might be the same.
 */
public enum ApplicationCategory {
  EVENT, // Tapahtuma
  CABLE_REPORT, // Johtoselvitys
  SHORT_TERM_RENTAL, // Lyhytaikainen maanvuokraus
  PLACEMENT_PERMIT, // Sijoituslupa
  DIG_NOTICE, // Kaivuuilmoitus
  AREA_RENTAL, // Aluevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS, // Väliaikainen liikennejärjestely
  NOTE // Muistiinpano
}
