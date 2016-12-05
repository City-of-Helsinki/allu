package fi.hel.allu.common.types;

/**
 * Application types define grouping for different application types. Each of
 * the groups is mapped to same set of data (see ApplicationExtension)
 */
public enum ApplicationType {
  EXCAVATION_ANNOUNCEMENT, // Kaivuilmoitus
  AREA_RENTAL, // Aluevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS, // Väliaikainen liikennejärjestely
  CABLE_REPORT, // Johtoselvitykset
  PLACEMENT_PERMIT, // Sijoitusluvat
  EVENT, // Tapahtuma
  SHORT_TERM_RENTAL, // Lyhytaikainen maanvuokraus
  NOTE // Muistiinpano
}
