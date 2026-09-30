import {KindsWithSpecifiers, SpecifierEntry, toKindsWithSpecifiers as specifiersToKindsWithSpecifiers} from './application-specifier';

export enum ApplicationKind {
  PROMOTION = 'PROMOTION',
  OUTDOOREVENT = 'OUTDOOREVENT',
  BIG_EVENT = 'BIG_EVENT', // Suuret tapahtumat
  BRIDGE_BANNER = 'BRIDGE_BANNER', // Banderollit silloissa
  BENJI = 'BENJI', // Benji-hyppylaite
  PROMOTION_OR_SALES = 'PROMOTION_OR_SALES', // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING = 'URBAN_FARMING', // Kaupunkiviljelypaikka
  KESKUSKATU_SALES = 'KESKUSKATU_SALES', // Keskuskadun myyntipaikka
  SUMMER_THEATER = 'SUMMER_THEATER', // Kesäteatterit
  DOG_TRAINING_FIELD = 'DOG_TRAINING_FIELD', // Koirakoulutuskentät
  DOG_TRAINING_EVENT = 'DOG_TRAINING_EVENT', // Koirakoulutustapahtuma
  SMALL_ART_AND_CULTURE = 'SMALL_ART_AND_CULTURE', // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE = 'SEASON_SALE', // Sesonkimyynti
  CIRCUS = 'CIRCUS', // Sirkus/tivolivierailu
  ART = 'ART', // Taideteos
  STORAGE_AREA = 'STORAGE_AREA', // Varastoalue
  SUMMER_TERRACE = 'SUMMER_TERRACE', // Kesäterassi
  WINTER_TERRACE = 'WINTER_TERRACE', // Talviterassi
  PARKLET = 'PARKLET', // Parklet
  MOBILE_SALES = 'MOBILE_SALES', // Liikkuva myynti/myyntiautot ja -vaunut
  STREET_AND_GREEN = 'STREET_AND_GREEN', // Katu- ja vihertyöt
  WATER_AND_SEWAGE = 'WATER_AND_SEWAGE', // Vesi / viemäri
  ELECTRICITY = 'ELECTRICITY', // Sähkö
  DATA_TRANSFER = 'DATA_TRANSFER', // Tiedonsiirto
  HEATING_COOLING = 'HEATING_COOLING', // Lämmitys/viilennys
  CONSTRUCTION = 'CONSTRUCTION', // Rakennus
  YARD = 'YARD', // Piha
  GEOLOGICAL_SURVEY = 'GEOLOGICAL_SURVEY', // Pohjatutkimus
  PROPERTY_RENOVATION = 'PROPERTY_RENOVATION', // kiinteistöremontti
  CONTAINER_BARRACK = 'CONTAINER_BARRACK', // kontti/parakki
  PHOTO_SHOOTING = 'PHOTO_SHOOTING', // kuvaus
  SNOW_WORK = 'SNOW_WORK', // lumenpudotus
  RELOCATION = 'RELOCATION', // muutto
  LIFTING = 'LIFTING', // nostotyö
  NEW_BUILDING_CONSTRUCTION = 'NEW_BUILDING_CONSTRUCTION', // uudisrakennuksen työmaa-alue
  ROLL_OFF = 'ROLL_OFF', // vaihtolava
  CHRISTMAS_TREE_SALES_AREA = 'CHRISTMAS_TREE_SALES_AREA', // Joulukuusenmyyntipaikka
  CITY_CYCLING_AREA = 'CITY_CYCLING_AREA', // Kaupunkipyöräpaikka
  AGILE_KIOSK_AREA = 'AGILE_KIOSK_AREA', // Ketterien kioskien myyntipaikka
  STATEMENT = 'STATEMENT', // Lausunto
  SNOW_HEAP_AREA = 'SNOW_HEAP_AREA', // Lumenkasauspaikka
  SNOW_GATHER_AREA = 'SNOW_GATHER_AREA', // Lumenvastaanottopaikka
  OTHER_SUBVISION_OF_STATE_AREA = 'OTHER_SUBVISION_OF_STATE_AREA', // Muun hallintokunnan alue
  MILITARY_EXCERCISE = 'MILITARY_EXCERCISE', // Sotaharjoitus
  WINTER_PARKING = 'WINTER_PARKING', // Talvipysäköinti
  REPAVING = 'REPAVING', // Uudelleenpäällystykset
  ELECTION_ADD_STAND = 'ELECTION_ADD_STAND', // Vaalimainosteline
  PUBLIC_EVENT = 'PUBLIC_EVENT', // Yleisötilaisuus
  OTHER = 'OTHER'
}

export const terraceKinds: ApplicationKind[] = [
  ApplicationKind.SUMMER_TERRACE,
  ApplicationKind.WINTER_TERRACE,
  ApplicationKind.PARKLET
];

export const disabledKinds: ApplicationKind[] = [
  ApplicationKind.DOG_TRAINING_EVENT
];

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
    'DISTRICT_COOLING',
    'GEO_HEATING'
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

export function mergeKindsWithSpecifiers(kinds: ApplicationKind[], specifierEntries: SpecifierEntry[]): KindsWithSpecifiers {
  return {
    ...toKindsWithSpecifiers(kinds),
    ...specifiersToKindsWithSpecifiers(specifierEntries)
  };
}

function toKindsWithSpecifiers(kinds: ApplicationKind[]): KindsWithSpecifiers {
  return kinds.reduce((prev, cur) => ({
    ...prev,
    ...{[cur]: []}
  }), {});
}
