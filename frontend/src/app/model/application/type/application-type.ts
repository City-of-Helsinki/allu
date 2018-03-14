import {ApplicationKindEntry, ApplicationKind} from './application-kind';
import {ApplicationSpecifier} from './application-specifier';
import {ArrayUtil} from '../../../util/array-util';
import {Some, Option} from '../../../util/option';

export enum ApplicationType {
  EXCAVATION_ANNOUNCEMENT, // Kaivuilmoitus
  AREA_RENTAL, // Aluevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS, // Väliaikainen liikennejärjestely
  CABLE_REPORT, // Johtoselvitykset
  PLACEMENT_CONTRACT, // Sijoitusopimukset
  EVENT, // Tapahtuma
  SHORT_TERM_RENTAL, // Lyhytaikainen maanvuokraus
  NOTE // Muistiinpano
}

export class ApplicationTypeEntry {
  constructor(public type: ApplicationType, private kinds: Array<ApplicationKindEntry>) {}

  public containsKind(kind: ApplicationKind): boolean {
    return this.kinds.map(k => k.kind).indexOf(kind) >= 0;
  }

  public kindEntryByType(kind: ApplicationKind): ApplicationKindEntry {
    return this.kinds.find(k => k.kind === kind);
  }

  get typeName() {
    return ApplicationType[this.type];
  }

  get applicationKindNames() {
    return this.kinds.map(k => ApplicationKind[k.kind]);
  }

  get applicationKindNamesSortedByTranslation(): Array<string> {
    return this.applicationKindNames.sort(ArrayUtil.naturalSortTranslated(['application.kind'], (kind: string) => kind));
  }
}

const commonApplicationKinds = [
  new ApplicationKindEntry(ApplicationKind.STREET_AND_GREEN, [
    ApplicationSpecifier.ASPHALT,
    ApplicationSpecifier.INDUCTION_LOOP,
    ApplicationSpecifier.COVER_STRUCTURE,
    ApplicationSpecifier.STREET_OR_PARK,
    ApplicationSpecifier.PAVEMENT,
    ApplicationSpecifier.TRAFFIC_LIGHT,
    ApplicationSpecifier.COMMERCIAL_DEVICE,
    ApplicationSpecifier.TRAFFIC_STOP,
    ApplicationSpecifier.BRIDGE,
    ApplicationSpecifier.OUTDOOR_LIGHTING
  ]),
  new ApplicationKindEntry(ApplicationKind.WATER_AND_SEWAGE, [
    ApplicationSpecifier.STORM_DRAIN,
    ApplicationSpecifier.WELL,
    ApplicationSpecifier.UNDERGROUND_DRAIN,
    ApplicationSpecifier.WATER_PIPE,
    ApplicationSpecifier.DRAIN
  ]),
  new ApplicationKindEntry(ApplicationKind.ELECTRICITY, [
    ApplicationSpecifier.DISTRIBUTION_CABINET,
    ApplicationSpecifier.ELECTRICITY_CABLE,
    ApplicationSpecifier.ELECTRICITY_WELL
  ]),
  new ApplicationKindEntry(ApplicationKind.DATA_TRANSFER, [
    ApplicationSpecifier.DISTRIBUTION_CABINET_OR_PILAR,
    ApplicationSpecifier.DATA_CABLE,
    ApplicationSpecifier.DATA_WELL
  ]),
  new ApplicationKindEntry(ApplicationKind.HEATING_COOLING, [
    ApplicationSpecifier.STREET_HEATING,
    ApplicationSpecifier.DISTRICT_HEATING,
    ApplicationSpecifier.DISTRICT_COOLING
  ]),
  new ApplicationKindEntry(ApplicationKind.CONSTRUCTION, [
    ApplicationSpecifier.GROUND_ROCK_ANCHOR,
    ApplicationSpecifier.UNDERGROUND_STRUCTURE,
    ApplicationSpecifier.UNDERGROUND_SPACE,
    ApplicationSpecifier.BASE_STRUCTURES,
    ApplicationSpecifier.DRILL_PILE,
    ApplicationSpecifier.CONSTRUCTION_EQUIPMENT,
    ApplicationSpecifier.CONSTRUCTION_PART,
    ApplicationSpecifier.GROUND_FROST_INSULATION,
    ApplicationSpecifier.SMOKE_HATCH_OR_PIPE,
    ApplicationSpecifier.STOP_OR_TRANSITION_SLAB,
    ApplicationSpecifier.SUPPORTING_WALL_OR_PILE
  ]),
  new ApplicationKindEntry(ApplicationKind.YARD, [
    ApplicationSpecifier.FENCE_OR_WALL,
    ApplicationSpecifier.DRIVEWAY,
    ApplicationSpecifier.STAIRS_RAMP,
    ApplicationSpecifier.SUPPORTING_WALL_OR_BANK
  ]),
  new ApplicationKindEntry(ApplicationKind.GEOLOGICAL_SURVEY, [
    ApplicationSpecifier.DRILLING,
    ApplicationSpecifier.TEST_HOLE,
    ApplicationSpecifier.GROUND_WATER_PIPE
  ]),
  new ApplicationKindEntry(ApplicationKind.OTHER, [
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM,
    ApplicationSpecifier.GAS_PIPE,
    ApplicationSpecifier.OTHER
  ])
];

export const excavationAnnouncement = new ApplicationTypeEntry(ApplicationType.EXCAVATION_ANNOUNCEMENT, commonApplicationKinds);

export const areaRental = new ApplicationTypeEntry(ApplicationType.AREA_RENTAL, [
  new ApplicationKindEntry(ApplicationKind.PROPERTY_RENOVATION),
  new ApplicationKindEntry(ApplicationKind.CONTAINER_BARRACK),
  new ApplicationKindEntry(ApplicationKind.PHOTO_SHOOTING),
  new ApplicationKindEntry(ApplicationKind.SNOW_WORK),
  new ApplicationKindEntry(ApplicationKind.RELOCATION),
  new ApplicationKindEntry(ApplicationKind.LIFTING),
  new ApplicationKindEntry(ApplicationKind.NEW_BUILDING_CONSTRUCTION),
  new ApplicationKindEntry(ApplicationKind.ROLL_OFF),
  new ApplicationKindEntry(ApplicationKind.OTHER)
]);

export const temporaryTrafficArrangements = new ApplicationTypeEntry(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, [
  new ApplicationKindEntry(ApplicationKind.PUBLIC_EVENT),
  new ApplicationKindEntry(ApplicationKind.OTHER)
]);

export const cableReport = new ApplicationTypeEntry(ApplicationType.CABLE_REPORT, commonApplicationKinds);

export const placementContract = new ApplicationTypeEntry(ApplicationType.PLACEMENT_CONTRACT, commonApplicationKinds);

export const event = new ApplicationTypeEntry(ApplicationType.EVENT, [
  new ApplicationKindEntry(ApplicationKind.PROMOTION),
  new ApplicationKindEntry(ApplicationKind.OUTDOOREVENT)
]);

export const shortTermRental = new ApplicationTypeEntry(ApplicationType.SHORT_TERM_RENTAL, [
  new ApplicationKindEntry(ApplicationKind.BRIDGE_BANNER),
  new ApplicationKindEntry(ApplicationKind.BENJI),
  new ApplicationKindEntry(ApplicationKind.PROMOTION_OR_SALES),
  new ApplicationKindEntry(ApplicationKind.URBAN_FARMING),
  new ApplicationKindEntry(ApplicationKind.KESKUSKATU_SALES),
  new ApplicationKindEntry(ApplicationKind.SUMMER_THEATER),
  new ApplicationKindEntry(ApplicationKind.DOG_TRAINING_FIELD),
  new ApplicationKindEntry(ApplicationKind.DOG_TRAINING_EVENT),
  new ApplicationKindEntry(ApplicationKind.SMALL_ART_AND_CULTURE),
  new ApplicationKindEntry(ApplicationKind.SEASON_SALE),
  new ApplicationKindEntry(ApplicationKind.CIRCUS),
  new ApplicationKindEntry(ApplicationKind.ART),
  new ApplicationKindEntry(ApplicationKind.STORAGE_AREA),
  new ApplicationKindEntry(ApplicationKind.OTHER)
]);

export const note = new ApplicationTypeEntry(ApplicationType.NOTE, [
  new ApplicationKindEntry(ApplicationKind.CHRISTMAS_TREE_SALES_AREA),
  new ApplicationKindEntry(ApplicationKind.CITY_CYCLING_AREA),
  new ApplicationKindEntry(ApplicationKind.AGILE_KIOSK_AREA),
  new ApplicationKindEntry(ApplicationKind.STATEMENT),
  new ApplicationKindEntry(ApplicationKind.SNOW_HEAP_AREA),
  new ApplicationKindEntry(ApplicationKind.SNOW_GATHER_AREA),
  new ApplicationKindEntry(ApplicationKind.OTHER_SUBVISION_OF_STATE_AREA),
  new ApplicationKindEntry(ApplicationKind.MILITARY_EXCERCISE),
  new ApplicationKindEntry(ApplicationKind.WINTER_PARKING),
  new ApplicationKindEntry(ApplicationKind.REPAVING),
  new ApplicationKindEntry(ApplicationKind.ELECTION_ADD_STAND),
  new ApplicationKindEntry(ApplicationKind.OTHER)
]);

export const applicationTypeEntries: Array<ApplicationTypeEntry> = [
  excavationAnnouncement,
  areaRental,
  temporaryTrafficArrangements,
  cableReport,
  placementContract,
  event,
  shortTermRental,
  note
];

export function typeEntryByType(type: string): Option<ApplicationTypeEntry> {
  const appType = ApplicationType[type];
  return Some(applicationTypeEntries.find(ts => ts.type === appType));
}

export function kindEntryByTypeAndKind(type: string, kind: string): Option<ApplicationKindEntry> {
  const kindType = ApplicationKind[kind];
  return typeEntryByType(type)
    .map(ts => ts.kindEntryByType(kindType));
}

export function hasMultipleKinds(type: ApplicationType): boolean {
  return hasSpecifiers(type);
}

export function hasSingleKind(type: ApplicationType): boolean {
  return !hasMultipleKinds(type);
}

export function hasSpecifiers(type: ApplicationType): boolean {
  return [
    ApplicationType.CABLE_REPORT,
    ApplicationType.EXCAVATION_ANNOUNCEMENT,
    ApplicationType.PLACEMENT_CONTRACT
  ].indexOf(type) >= 0;
}

export const creatableTypes = [
  ApplicationType.EVENT,
  ApplicationType.SHORT_TERM_RENTAL,
  ApplicationType.NOTE,
  ApplicationType.PLACEMENT_CONTRACT,
  ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS
];

