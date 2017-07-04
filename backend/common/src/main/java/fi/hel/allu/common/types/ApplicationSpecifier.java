package fi.hel.allu.common.types;

/**
 * Additional specifiers for certain application kinds
 */
public enum ApplicationSpecifier {
  // Katu- ja vihertyöt
  ASPHALT(ApplicationKind.STREET_AND_GREEN), // Asfaltointityö
  INDUCTION_LOOP(ApplicationKind.STREET_AND_GREEN), // Induktiosilmukka
  COVER_STRUCTURE(ApplicationKind.STREET_AND_GREEN), // Kansisto
  STREET_OR_PARK(ApplicationKind.STREET_AND_GREEN), // Katu tai puisto
  PAVEMENT(ApplicationKind.STREET_AND_GREEN), // Kiveystyö
  TRAFFIC_LIGHT(ApplicationKind.STREET_AND_GREEN), // Liikennevalo
  COMMERCIAL_DEVICE(ApplicationKind.STREET_AND_GREEN), // Mainoslaite
  TRAFFIC_STOP(ApplicationKind.STREET_AND_GREEN), // Pysäkkikatos
  BRIDGE(ApplicationKind.STREET_AND_GREEN), // Silta
  OUTDOOR_LIGHTING(ApplicationKind.STREET_AND_GREEN), // Ulkovalaistus
  // Vesi / viemäri
  STORM_DRAIN(ApplicationKind.WATER_AND_SEWAGE), // Hulevesi
  WELL(ApplicationKind.WATER_AND_SEWAGE), // Kaivo
  UNDERGROUND_DRAIN(ApplicationKind.WATER_AND_SEWAGE), // Salaoja
  WATER_PIPE(ApplicationKind.WATER_AND_SEWAGE), // Vesijohto
  DRAIN(ApplicationKind.WATER_AND_SEWAGE), // Viemäri
  // Sähkö
  DISTRIBUTION_CABINET(ApplicationKind.ELECTRICITY), // Jakokaappi
  ELECTRICITY_CABLE(ApplicationKind.ELECTRICITY), // Kaapeli
  ELECTRICITY_WELL(ApplicationKind.ELECTRICITY), // Kaivo
  // Tiedonsiirto
  DISTRIBUTION_CABINET_OR_PILAR(ApplicationKind.DATA_TRANSFER), // Jakokaappi/-pilari
  DATA_CABLE(ApplicationKind.DATA_TRANSFER), // Kaapeli
  DATA_WELL(ApplicationKind.DATA_TRANSFER), // Kaivo
  // Lämmitys/viilennys
  STREET_HEATING(ApplicationKind.HEATING_COOLING), // Katulämmitys
  DISTRICT_HEATING(ApplicationKind.HEATING_COOLING), // Kaukolämpö
  DISTRICT_COOLING(ApplicationKind.HEATING_COOLING), // Kaukokylmä
  // Rakennus
  GROUND_ROCK_ANCHOR(ApplicationKind.CONSTRUCTION), // Maa- / kallioankkuri
  UNDERGROUND_STRUCTURE(ApplicationKind.CONSTRUCTION), // Maanalainen rakenne
  UNDERGROUND_SPACE(ApplicationKind.CONSTRUCTION), // Maanalainen tila
  BASE_STRUCTURES(ApplicationKind.CONSTRUCTION), // Perusrakenteet
  DRILL_PILE(ApplicationKind.CONSTRUCTION), // Porapaalu
  CONSTRUCTION_EQUIPMENT(ApplicationKind.CONSTRUCTION), // Rakennuksen laite/varuste
  CONSTRUCTION_PART(ApplicationKind.CONSTRUCTION), // Rakennuksen osa
  GROUND_FROST_INSULATION(ApplicationKind.CONSTRUCTION), // Routaeriste
  SMOKE_HATCH_OR_PIPE(ApplicationKind.CONSTRUCTION), // Savunpoistoluukku/-putki, IV-putki
  STOP_OR_TRANSITION_SLAB(ApplicationKind.CONSTRUCTION), // Sulku-/siirtymälaatta
  SUPPORTING_WALL_OR_PILE(ApplicationKind.CONSTRUCTION), // Tukiseinä/-paalu
  // Piha
  FENCE_OR_WALL(ApplicationKind.YARD), // Aita, muuri, penger
  DRIVEWAY(ApplicationKind.YARD), // Kulkutie
  STAIRS_RAMP(ApplicationKind.YARD), // Portaat, luiska tms.
  SUPPORTING_WALL_OR_BANK(ApplicationKind.YARD), // Tukimuuri/-penger, lujitemaamuuri
  // Pohjatutkimus
  DRILLING(ApplicationKind.GEOLOGICAL_SURVEY), // Kairaus
  TEST_HOLE(ApplicationKind.GEOLOGICAL_SURVEY), // Koekuoppa
  GROUND_WATER_PIPE(ApplicationKind.GEOLOGICAL_SURVEY), // Pohjavesiputki
  // Muu
  ABSORBING_SEWAGE_SYSTEM(ApplicationKind.OTHER_CABLE_REPORT), // Imujätejärjestelmä
  GAS_PIPE(ApplicationKind.OTHER_CABLE_REPORT), // Kaasujohto
  OTHER(ApplicationKind.OTHER_CABLE_REPORT); // Muu

  public ApplicationKind getKind() {
    return kind;
  }

  private final ApplicationKind kind;

  private ApplicationSpecifier(ApplicationKind kind) {
    this.kind = kind;
  }

}
