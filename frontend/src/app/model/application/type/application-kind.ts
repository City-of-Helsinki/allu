import {KindsWithSpecifiers} from './application-specifier';

export enum ApplicationKind {
  PROMOTION,
  OUTDOOREVENT,
  BIG_EVENT, // Suuret tapahtumat
  BRIDGE_BANNER, // Banderollit silloissa
  BENJI, // Benji-hyppylaite
  PROMOTION_OR_SALES, // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING, // Kaupunkiviljelypaikka
  KESKUSKATU_SALES, // Keskuskadun myyntipaikka
  SUMMER_THEATER, // Kesäteatterit
  DOG_TRAINING_FIELD, // Koirakoulutuskentät
  DOG_TRAINING_EVENT, // Koirakoulutustapahtuma
  SMALL_ART_AND_CULTURE, // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE, // Sesonkimyynti
  CIRCUS, // Sirkus/tivolivierailu
  ART, // Taideteos
  STORAGE_AREA, // Varastoalue
  STREET_AND_GREEN, // Katu- ja vihertyöt
  WATER_AND_SEWAGE, // Vesi / viemäri
  ELECTRICITY, // Sähkö
  DATA_TRANSFER, // Tiedonsiirto
  HEATING_COOLING, // Lämmitys/viilennys
  CONSTRUCTION, // Rakennus
  YARD, // Piha
  GEOLOGICAL_SURVEY, // Pohjatutkimus
  PROPERTY_RENOVATION, // kiinteistöremontti
  CONTAINER_BARRACK, // kontti/parakki
  PHOTO_SHOOTING, // kuvaus
  SNOW_WORK, // lumenpudotus
  RELOCATION, // muutto
  LIFTING, // nostotyö
  NEW_BUILDING_CONSTRUCTION, // uudisrakennuksen työmaa-alue
  ROLL_OFF, // vaihtolava
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
  PUBLIC_EVENT, // Yleisötilaisuus
  OTHER
}

export const commonKinds: KindsWithSpecifiers = {
  STREET_AND_GREEN: [
    'ASPHALT',
    'INDUCTION_LOOP',
    'COVER_STRUCTURE',
    'STREET_OR_PARK',
    'PAVEMENT',
    'TRAFFIC_LIGHT',
    'COMMERCIAL_DEVICE',
    'TRAFFIC_STOP',
    'BRIDGE',
    'OUTDOOR_LIGHTING'
  ],
  WATER_AND_SEWAGE: [
    'STORM_DRAIN',
    'WELL',
    'UNDERGROUND_DRAIN',
    'WATER_PIPE',
    'DRAIN'
  ],
  ELECTRICITY: [
    'DISTRIBUTION_CABINET',
    'ELECTRICITY_CABLE',
    'ELECTRICITY_WELL'
  ],
  DATA_TRANSFER: [
    'DISTRIBUTION_CABINET_OR_PILAR',
    'DATA_CABLE',
    'DATA_WELL'
  ],
  HEATING_COOLING: [
    'STREET_HEATING',
    'DISTRICT_HEATING',
    'DISTRICT_COOLING'
  ],
  CONSTRUCTION: [
    'GROUND_ROCK_ANCHOR',
    'UNDERGROUND_STRUCTURE',
    'UNDERGROUND_SPACE',
    'BASE_STRUCTURES',
    'DRILL_PILE',
    'CONSTRUCTION_EQUIPMENT',
    'CONSTRUCTION_PART',
    'GROUND_FROST_INSULATION',
    'SMOKE_HATCH_OR_PIPE',
    'STOP_OR_TRANSITION_SLAB',
    'SUPPORTING_WALL_OR_PILE'
  ],
  YARD: [
    'FENCE_OR_WALL',
    'DRIVEWAY',
    'STAIRS_RAMP',
    'SUPPORTING_WALL_OR_BANK'
  ],
  GEOLOGICAL_SURVEY: [
    'DRILLING',
    'TEST_HOLE',
    'GROUND_WATER_PIPE'
  ],
  OTHER: [
    'ABSORBING_SEWAGE_SYSTEM',
    'GAS_PIPE',
    'OTHER'
  ]
};

export function drawingAllowedForKind(kind: ApplicationKind): boolean {
  return ![ApplicationKind.BRIDGE_BANNER, ApplicationKind.DOG_TRAINING_EVENT, ApplicationKind.DOG_TRAINING_FIELD]
    .some(k => k === kind);
}
