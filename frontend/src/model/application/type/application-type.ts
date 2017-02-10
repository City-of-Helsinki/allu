import {ApplicationKindStructure, ApplicationKind} from './application-kind';
import {ApplicationSpecifier} from './application-specifier';
import {ArrayUtil} from '../../../util/array-util';

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

export class ApplicationTypeStructure {
  constructor(public type: ApplicationType, private kinds: Array<ApplicationKindStructure>) {}

  public containsKind(kind: ApplicationKind): boolean {
    return this.kinds.map(k => k.kind).indexOf(kind) >= 0;
  }

  public structureByKind(kind: ApplicationKind): ApplicationKindStructure {
    return this.kinds.find(k => k.kind === kind);
  }

  get typeName() {
    return ApplicationType[this.type];
  }

  get applicationKindNames() {
    return this.kinds.map(k => ApplicationKind[k.kind]);
  }

  get applicationKindNamesSortedByTranslation() {
    return this.applicationKindNames.sort(ArrayUtil.naturalSortTranslated(['application.kind'], (kind: string) => kind));
  }
}

const commonApplicationKinds = [
  new ApplicationKindStructure(ApplicationKind.STREET_AND_GREEN, [
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
  new ApplicationKindStructure(ApplicationKind.WATER_AND_SEWAGE, [
    ApplicationSpecifier.STORM_DRAIN,
    ApplicationSpecifier.WELL,
    ApplicationSpecifier.UNDERGROUND_DRAIN,
    ApplicationSpecifier.WATER_PIPE,
    ApplicationSpecifier.DRAIN
  ]),
  new ApplicationKindStructure(ApplicationKind.ELECTRICITY, [
    ApplicationSpecifier.DISTRIBUTION_CABINET,
    ApplicationSpecifier.ELECTRICITY_CABLE,
    ApplicationSpecifier.ELECTRICITY_WELL
  ]),
  new ApplicationKindStructure(ApplicationKind.DATA_TRANSFER, [
    ApplicationSpecifier.DISTRIBUTION_CABINET_OR_PILAR,
    ApplicationSpecifier.DATA_CABLE,
    ApplicationSpecifier.DATA_WELL
  ]),
  new ApplicationKindStructure(ApplicationKind.HEATING_COOLING, [
    ApplicationSpecifier.STREET_HEATING,
    ApplicationSpecifier.DISTRICT_HEATING,
    ApplicationSpecifier.DISTRICT_COOLING
  ]),
  new ApplicationKindStructure(ApplicationKind.CONSTRUCTION, [
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
  new ApplicationKindStructure(ApplicationKind.YARD, [
    ApplicationSpecifier.FENCE_OR_WALL,
    ApplicationSpecifier.DRIVEWAY,
    ApplicationSpecifier.STAIRS_RAMP,
    ApplicationSpecifier.SUPPORTING_WALL_OR_BANK
  ]),
  new ApplicationKindStructure(ApplicationKind.GEOLOGICAL_SURVEY, [
    ApplicationSpecifier.DRILLING,
    ApplicationSpecifier.TEST_HOLE,
    ApplicationSpecifier.GROUND_WATER_PIPE
  ]),
  new ApplicationKindStructure(ApplicationKind.OTHER_CABLE_REPORT, [
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM,
    ApplicationSpecifier.GAS_PIPE,
    ApplicationSpecifier.OTHER
  ])
];

export const excavationAnnouncement = new ApplicationTypeStructure(ApplicationType.EXCAVATION_ANNOUNCEMENT, commonApplicationKinds);

export const areaRental = new ApplicationTypeStructure(ApplicationType.AREA_RENTAL, []);

export const temporaryTrafficArrangements = new ApplicationTypeStructure(
  ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, commonApplicationKinds);

export const cableReport = new ApplicationTypeStructure(ApplicationType.CABLE_REPORT, commonApplicationKinds);

export const placementContract = new ApplicationTypeStructure(ApplicationType.PLACEMENT_CONTRACT, commonApplicationKinds);

export const event = new ApplicationTypeStructure(ApplicationType.EVENT, [
  new ApplicationKindStructure(ApplicationKind.PROMOTION),
  new ApplicationKindStructure(ApplicationKind.OUTDOOREVENT)
]);

export const shortTermRental = new ApplicationTypeStructure(ApplicationType.SHORT_TERM_RENTAL, [
  new ApplicationKindStructure(ApplicationKind.BRIDGE_BANNER),
  new ApplicationKindStructure(ApplicationKind.BENJI),
  new ApplicationKindStructure(ApplicationKind.PROMOTION_OR_SALES),
  new ApplicationKindStructure(ApplicationKind.URBAN_FARMING),
  new ApplicationKindStructure(ApplicationKind.MAIN_STREET_SALES),
  new ApplicationKindStructure(ApplicationKind.SUMMER_THEATER),
  new ApplicationKindStructure(ApplicationKind.DOG_TRAINING_FIELD),
  new ApplicationKindStructure(ApplicationKind.DOG_TRAINING_EVENT),
  new ApplicationKindStructure(ApplicationKind.CARGO_CONTAINER),
  new ApplicationKindStructure(ApplicationKind.SMALL_ART_AND_CULTURE),
  new ApplicationKindStructure(ApplicationKind.SEASON_SALE),
  new ApplicationKindStructure(ApplicationKind.CIRCUS),
  new ApplicationKindStructure(ApplicationKind.ART),
  new ApplicationKindStructure(ApplicationKind.STORAGE_AREA),
  new ApplicationKindStructure(ApplicationKind.OTHER_SHORT_TERM_RENTAL)
]);

export const note = new ApplicationTypeStructure(ApplicationType.NOTE, [
  new ApplicationKindStructure(ApplicationKind.CHRISTMAS_TREE_SALES_AREA),
  new ApplicationKindStructure(ApplicationKind.CITY_CYCLING_AREA),
  new ApplicationKindStructure(ApplicationKind.AGILE_KIOSK_AREA),
  new ApplicationKindStructure(ApplicationKind.STATEMENT),
  new ApplicationKindStructure(ApplicationKind.SNOW_HEAP_AREA),
  new ApplicationKindStructure(ApplicationKind.SNOW_GATHER_AREA),
  new ApplicationKindStructure(ApplicationKind.OTHER_SUBVISION_OF_STATE_AREA),
  new ApplicationKindStructure(ApplicationKind.MILITARY_EXCERCISE),
  new ApplicationKindStructure(ApplicationKind.WINTER_PARKING),
  new ApplicationKindStructure(ApplicationKind.REPAVING),
  new ApplicationKindStructure(ApplicationKind.ELECTION_ADD_STAND),
  new ApplicationKindStructure(ApplicationKind.NOTE_OTHER)
]);

export const applicationTypes: Array<ApplicationTypeStructure> = [
  excavationAnnouncement,
  areaRental,
  temporaryTrafficArrangements,
  cableReport,
  placementContract,
  event,
  shortTermRental,
  note
];


