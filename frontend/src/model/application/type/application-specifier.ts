export enum ApplicationSpecifier {
  // Katu- ja vihertyöt
  ASPHALT, // Asfaltointityö
  INDUCTION_LOOP, // Induktiosilmukka
  COVER_STRUCTURE, // Kansisto
  STREET_OR_PARK, // Katu tai puisto
  PAVEMENT, // Kiveystyö
  TRAFFIC_LIGHT, // Liikennevalo
  COMMERCIAL_DEVICE, // Mainoslaite
  TRAFFIC_STOP, // Pysäkkikatos
  BRIDGE, // Silta
  OUTDOOR_LIGHTING, // Ulkovalaistus
  // Vesi / viemäri
  STORM_DRAIN, // Hulevesi
  WELL, // Kaivo
  UNDERGROUND_DRAIN,  // Salaoja
  WATER_PIPE, // Vesijohto
  DRAIN, // Viemäri
  // Sähkö
  DISTRIBUTION_CABINET, // Jakokaappi
  ELECTRICITY_CABLE, // Kaapeli
  ELECTRICITY_WELL, // Kaivo
  // Tiedonsiirto
  DISTRIBUTION_CABINET_OR_PILAR, // Jakokaappi/-pilari
  DATA_CABLE, // Kaapeli
  DATA_WELL, // Kaivo
  // Lämmitys/viilennys
  STREET_HEATING, // Katulämmitys
  DISTRICT_HEATING, // Kaukolämpö
  DISTRICT_COOLING, // Kaukokylmä
  // Rakennus
  GROUND_ROCK_ANCHOR, // Maa- / kallioankkuri
  UNDERGROUND_STRUCTURE, // Maanalainen rakenne
  UNDERGROUND_SPACE, // Maanalainen tila
  BASE_STRUCTURES, // Perusrakenteet
  DRILL_PILE, // Porapaalu
  CONSTRUCTION_EQUIPMENT, //  Rakennuksen laite/varuste
  CONSTRUCTION_PART, // Rakennuksen osa
  GROUND_FROST_INSULATION, // Routaeriste
  SMOKE_HATCH_OR_PIPE,  // Savunpoistoluukku/-putki, IV-putki
  STOP_OR_TRANSITION_SLAB,  // Sulku-/siirtymälaatta
  SUPPORTING_WALL_OR_PILE,  // Tukiseinä/-paalu
  // Piha
  FENCE_OR_WALL, // Aita, muuri, penger
  DRIVEWAY, // Kulkutie
  STAIRS_RAMP,  // Portaat, luiska tms.
  SUPPORTING_WALL_OR_BANK,   // Tukimuuri/-penger, lujitemaamuuri
  // Pohjatutkimus
  DRILLING, // Kairaus
  TEST_HOLE, // Koekuoppa
  GROUND_WATER_PIPE, // Pohjavesiputki
  // Muu
  ABSORBING_SEWAGE_SYSTEM, // Imujätejärjestelmä
  GAS_PIPE,  // Kaasujohto
  OTHER  // Muu
}
