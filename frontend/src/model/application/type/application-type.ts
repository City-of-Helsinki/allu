import {ApplicationKindStructure, ApplicationKind} from './application-kind';
import {ApplicationSpecifier} from './application-specifier';

export enum ApplicationType {
  EXCAVATION_ANNOUNCEMENT, // Kaivuilmoitus
  AREA_RENTAL, // Aluevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS, // Väliaikainen liikennejärjestely
  CABLE_REPORT, // Johtoselvitykset
  PLACEMENT_PERMIT, // Sijoitusluvat
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
}

export const excavationAnnouncement = new ApplicationTypeStructure(ApplicationType.EXCAVATION_ANNOUNCEMENT, []);

export const areaRental = new ApplicationTypeStructure(ApplicationType.AREA_RENTAL, []);

export const temporaryTrafficArrangements = new ApplicationTypeStructure(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, []);

export const cableReport = new ApplicationTypeStructure(ApplicationType.CABLE_REPORT, [
  new ApplicationKindStructure(ApplicationKind.CITY_STREET_AND_GREEN, [
    ApplicationSpecifier.CITY_STREET_AND_GREEN_CONSTRUCTION,
    ApplicationSpecifier.CITY_STREET_AND_GREEN_MAINTENANCE,
    ApplicationSpecifier.ASPHALT,
    ApplicationSpecifier.PAVEMENT,
    ApplicationSpecifier.BRIDGE
  ]),
  new ApplicationKindStructure(ApplicationKind.WATER_AND_SEWAGE, [
    ApplicationSpecifier.STORM_DRAIN_CONSTRUCTION,
    ApplicationSpecifier.STORM_DRAIN_MAINTENANCE,
    ApplicationSpecifier.WATER_PIPE_CONSTRUCTION,
    ApplicationSpecifier.WATER_PIPE_MAINTENANCE,
    ApplicationSpecifier.WATER_PIPE_LEAK_REPAIR,
    ApplicationSpecifier.DRAIN_CONSTRUCTION,
    ApplicationSpecifier.DRAIN_MAINTENANCE,
    ApplicationSpecifier.DRAIN_LEAK_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.HKL, [
    ApplicationSpecifier.HKL_STOP_WORK,
    ApplicationSpecifier.HKL_OTHER_WORK
  ]),
  new ApplicationKindStructure(ApplicationKind.ELECTRIC_CABLE, [
    ApplicationSpecifier.CABLE_CONSTRUCTION,
    ApplicationSpecifier.CABLE_MAINTENANCE,
    ApplicationSpecifier.CABLE_REPAIR,
    ApplicationSpecifier.OUTDOOR_LIGHTING,
    ApplicationSpecifier.TRAFFIC_LIGHTS
  ]),
  new ApplicationKindStructure(ApplicationKind.DISTRICT_HEATING, [
    ApplicationSpecifier.DISTRICT_HEATING_CONSTRUCTION,
    ApplicationSpecifier.DISTRICT_HEATING_MAINTENANCE,
    ApplicationSpecifier.DISTRICT_HEATING_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.DISTRICT_COOLING, [
    ApplicationSpecifier.DISTRICT_COOLING_CONSTRUCTION,
    ApplicationSpecifier.DISTRICT_COOLING_MAINTENANCE,
    ApplicationSpecifier.DISTRICT_COOLING_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.TELECOMMUNICATION, [
    ApplicationSpecifier.TELECOMMUNICATION_CONSTRUCTION,
    ApplicationSpecifier.TELECOMMUNICATION_MAINTENANCE,
    ApplicationSpecifier.TELECOMMUNICATION_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.GAS, [
    ApplicationSpecifier.GAS_CONSTRUCTION,
    ApplicationSpecifier.GAS_MAINTENANCE,
    ApplicationSpecifier.GAS_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.AD_PILLARS_AND_STOPS, [
    ApplicationSpecifier.AD_STOPS,
    ApplicationSpecifier.AD_BILLBOARDS_AND_PILLARS
  ]),
  new ApplicationKindStructure(ApplicationKind.PROPERTY_MERGER, [
    ApplicationSpecifier.PROPERTY_MERGER_WATER,
    ApplicationSpecifier.PROPERTY_MERGER_DRAIN,
    ApplicationSpecifier.PROPERTY_MERGER_DISTRICT_HEATING,
    ApplicationSpecifier.PROPERTY_MERGER_DISTRICT_COOLING,
    ApplicationSpecifier.PROPERTY_MERGER_ELECTRICITY,
    ApplicationSpecifier.PROPERTY_MERGER_TELECOMMUNICATION,
    ApplicationSpecifier.PROPERTY_MERGER_ENTRANCE,
    ApplicationSpecifier.PROPERTY_MERGER_GAS,
    ApplicationSpecifier.PROPERTY_MERGER_STORM_DRAIN,
    ApplicationSpecifier.PROPERTY_MERGER_ABSORBING_SEWAGE_SYSTEM
  ]),
  new ApplicationKindStructure(ApplicationKind.SOIL_INVESTIGATION, [
    ApplicationSpecifier.SOIL_INVESTIGATION_DRILLING,
    ApplicationSpecifier.SOIL_INVESTIGATION_PIPING,
    ApplicationSpecifier.SOIL_INVESTIGATION_TEST_HOLES,
    ApplicationSpecifier.SOIL_INVESTIGATION_OTHER
  ]),
  new ApplicationKindStructure(ApplicationKind.JOINT_MUNICIPAL_INFRASTRUCTURE, [
    ApplicationSpecifier.JOINT_MUNICIPAL_INFRASTRUCTURE_CONSTRUCTION,
    ApplicationSpecifier.JOINT_MUNICIPAL_INFRASTRUCTURE_MAINTENANCE
  ]),
  new ApplicationKindStructure(ApplicationKind.ABSORBING_SEWAGE_SYSTEM, [
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_CONSTRUCTION,
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_MAINTENANCE,
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_REPAIR
  ]),
  new ApplicationKindStructure(ApplicationKind.UNDERGROUND_CONSTRUCTION, [
    ApplicationSpecifier.UNDERGROUND_CONSTRUCTION_SPACE,
    ApplicationSpecifier.UNDERGROUND_CONSTRUCTION_STRUCTURE
  ]),
  new ApplicationKindStructure(ApplicationKind.OTHER_CABLE_REPORT, [
    ApplicationSpecifier.OTHER_CABLE_WORK_DEMOUNTABLE_PLATFORM,
    ApplicationSpecifier.OTHER_CABLE_WORK_LIFT,
    ApplicationSpecifier.OTHER_CABLE_WORK_SNOW_CLEARING,
    ApplicationSpecifier.OTHER_CABLE_WORK_PUBLIC_OCCASION,
    ApplicationSpecifier.OTHER_CABLE_WORK_MAPPING,
    ApplicationSpecifier.OTHER_CABLE_WORK_RELOCATION,
    ApplicationSpecifier.OTHER_CABLE_WORK_ESTATE_REPAIR,
    ApplicationSpecifier.OTHER_CABLE_WORK_OTHER_TRAFFIC_ARRANGEMENTS
  ])
]);

export const placementPermit = new ApplicationTypeStructure(ApplicationType.PLACEMENT_PERMIT, []);

export const event = new ApplicationTypeStructure(ApplicationType.EVENT, [
  new ApplicationKindStructure(ApplicationKind.PROMOTION),
  new ApplicationKindStructure(ApplicationKind.OUTDOOREVENT),
  new ApplicationKindStructure(ApplicationKind.ELECTION)
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

export const note = new ApplicationTypeStructure(ApplicationType.NOTE, []);

export const applicationTypes: Array<ApplicationTypeStructure> = [
  excavationAnnouncement,
  areaRental,
  temporaryTrafficArrangements,
  cableReport,
  placementPermit,
  event,
  shortTermRental
];


