package fi.hel.allu.common.domain.types;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application kind specifies the subtype of application.
 */
public enum ApplicationKind {
  PROMOTION(ApplicationType.EVENT),
  OUTDOOREVENT(ApplicationType.EVENT),
  BIG_EVENT(ApplicationType.EVENT),
  BRIDGE_BANNER(ApplicationType.SHORT_TERM_RENTAL), // Banderollit silloissa
  BENJI(ApplicationType.SHORT_TERM_RENTAL), // Benji-hyppylaite
  PROMOTION_OR_SALES(ApplicationType.SHORT_TERM_RENTAL), // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING(ApplicationType.SHORT_TERM_RENTAL), // Kaupunkiviljelypaikka
  KESKUSKATU_SALES(ApplicationType.SHORT_TERM_RENTAL), // Keskuskadun myyntipaikka
  SUMMER_THEATER(ApplicationType.SHORT_TERM_RENTAL), // Kesäteatterit
  DOG_TRAINING_FIELD(ApplicationType.SHORT_TERM_RENTAL), // Koirakoulutuskentät
  DOG_TRAINING_EVENT(ApplicationType.SHORT_TERM_RENTAL), // Koirakoulutustapahtuma
  SMALL_ART_AND_CULTURE(ApplicationType.SHORT_TERM_RENTAL), // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE(ApplicationType.SHORT_TERM_RENTAL), // Sesonkimyynti
  CIRCUS(ApplicationType.SHORT_TERM_RENTAL), // Sirkus/tivolivierailu
  ART(ApplicationType.SHORT_TERM_RENTAL), // Taideteos
  STORAGE_AREA(ApplicationType.SHORT_TERM_RENTAL, ApplicationType.AREA_RENTAL), // Varastoalue
  SUMMER_TERRACE(ApplicationType.SHORT_TERM_RENTAL), // Kesäterassi
  WINTER_TERRACE(ApplicationType.SHORT_TERM_RENTAL), // Talviterassi
  PARKLET(ApplicationType.SHORT_TERM_RENTAL), // Parklet
  MOBILE_SALES(ApplicationType.SHORT_TERM_RENTAL), // Liikkuva myynti/myyntiautot ja -vaunut
  STREET_AND_GREEN(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Katu- ja vihertyöt
  WATER_AND_SEWAGE(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Vesi / viemäri
  ELECTRICITY(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Sähkö
  DATA_TRANSFER(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Tiedonsiirto
  HEATING_COOLING(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Lämmitys/viilennys
  CONSTRUCTION(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Rakennus
  YARD(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.PLACEMENT_CONTRACT), // Piha
  GEOLOGICAL_SURVEY(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT), // Pohjatutkimus
  PROPERTY_RENOVATION(ApplicationType.AREA_RENTAL),        // kiinteistöremontti
  CONTAINER_BARRACK(ApplicationType.AREA_RENTAL),          // kontti/parakki
  PHOTO_SHOOTING(ApplicationType.AREA_RENTAL),             // kuvaus
  SNOW_WORK(ApplicationType.AREA_RENTAL),                  // lumenpudotus
  RELOCATION(ApplicationType.AREA_RENTAL),                 // muutto
  LIFTING(ApplicationType.AREA_RENTAL),                    // nostotyö
  NEW_BUILDING_CONSTRUCTION(ApplicationType.AREA_RENTAL),  // uudisrakennuksen työmaa-alue
  ROLL_OFF(ApplicationType.AREA_RENTAL),                   // vaihtolava
  CHRISTMAS_TREE_SALES_AREA(ApplicationType.NOTE), // Joulukuusenmyyntipaikka
  CITY_CYCLING_AREA(ApplicationType.NOTE), // Kaupunkipyöräpaikka
  AGILE_KIOSK_AREA(ApplicationType.NOTE), // Ketterien kioskien myyntipaikka
  STATEMENT(ApplicationType.NOTE), // Lausunto
  SNOW_HEAP_AREA(ApplicationType.NOTE), // Lumenkasauspaikka
  SNOW_GATHER_AREA(ApplicationType.NOTE), // Lumenvastaanottopaikka
  OTHER_SUBVISION_OF_STATE_AREA(ApplicationType.NOTE), // Muun hallintokunnan alue
  MILITARY_EXCERCISE(ApplicationType.NOTE), // Sotaharjoitus
  WINTER_PARKING(ApplicationType.NOTE), // Talvipysäköinti
  REPAVING(ApplicationType.NOTE), // Uudelleenpäällystykset
  ELECTION_ADD_STAND(ApplicationType.NOTE), // Vaalimainosteline
  PUBLIC_EVENT(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, ApplicationType.AREA_RENTAL), // Yleisötilaisuus
  OTHER(ApplicationType.SHORT_TERM_RENTAL, ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.PLACEMENT_CONTRACT, ApplicationType.AREA_RENTAL,
      ApplicationType.NOTE, ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS); // Muu

  private final List<ApplicationType> types;

  /**
   * Get the allowed application type for this application kind
   */
  public List<ApplicationType> getTypes() {
    return types;
  }

  private ApplicationKind(ApplicationType... types) {
    this.types = Arrays.asList(types);
  }

  public static List<ApplicationKind> forApplicationType(ApplicationType type) {
    return Arrays.stream(ApplicationKind.values()).filter(k -> k.types.contains(type)).collect(Collectors.toList());
  }

  public boolean isTerrace() {
    return this == SUMMER_TERRACE || this == WINTER_TERRACE || this == PARKLET;
  }
}
