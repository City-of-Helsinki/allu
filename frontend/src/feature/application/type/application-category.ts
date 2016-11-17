import {ApplicationType} from '../../../model/application/type/application-type';
import {ApplicationSpecifier} from '../../../model/application/type/application-specifier';

export enum ApplicationCategoryType {
  STREET,
  EVENT,
  SHORT_TERM_RENTAL,
  CABLE_REPORT
}

export class TypeStructure {
  constructor(public type: ApplicationType, private specifiers?: Array<ApplicationSpecifier>) {
    this.specifiers = specifiers || [];
  }

  get applicationSpecifierNames() {
    return this.specifiers.map(s => ApplicationSpecifier[s]);
  }
}

export class ApplicationCategory {
  constructor(public categoryType: ApplicationCategoryType, private typeStructures: Array<TypeStructure>) {}

  public containsType(applicationType: ApplicationType): boolean {
    return this.typeStructures.map(t => t.type).indexOf(applicationType) >= 0;
  }

  public structureByType(applicationType: ApplicationType): TypeStructure {
    return this.typeStructures.find(ts => ts.type === applicationType);
  }

  get categoryTypeName() {
    return ApplicationCategoryType[this.categoryType];
  }

  get applicationTypeNames() {
    return this.typeStructures.map(t => ApplicationType[t.type]);
  }
}

export const street = new ApplicationCategory(ApplicationCategoryType.STREET, [
  new TypeStructure(ApplicationType.EXCAVATION_ANNOUNCEMENT),
  new TypeStructure(ApplicationType.AREA_RENTAL),
  new TypeStructure(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS)
]);

export const event = new ApplicationCategory(ApplicationCategoryType.EVENT, [
  new TypeStructure(ApplicationType.PROMOTION),
  new TypeStructure(ApplicationType.OUTDOOREVENT),
  new TypeStructure(ApplicationType.ELECTION)
]);

export const shortTermRental = new ApplicationCategory(ApplicationCategoryType.SHORT_TERM_RENTAL, [
  new TypeStructure(ApplicationType.BRIDGE_BANNER),
  new TypeStructure(ApplicationType.BENJI),
  new TypeStructure(ApplicationType.PROMOTION_OR_SALES),
  new TypeStructure(ApplicationType.URBAN_FARMING),
  new TypeStructure(ApplicationType.MAIN_STREET_SALES),
  new TypeStructure(ApplicationType.SUMMER_THEATER),
  new TypeStructure(ApplicationType.DOG_TRAINING_FIELD),
  new TypeStructure(ApplicationType.DOG_TRAINING_EVENT),
  new TypeStructure(ApplicationType.CARGO_CONTAINER),
  new TypeStructure(ApplicationType.SMALL_ART_AND_CULTURE),
  new TypeStructure(ApplicationType.SEASON_SALE),
  new TypeStructure(ApplicationType.CIRCUS),
  new TypeStructure(ApplicationType.ART),
  new TypeStructure(ApplicationType.STORAGE_AREA),
  new TypeStructure(ApplicationType.OTHER_SHORT_TERM_RENTAL)
]);

export const cableReport = new ApplicationCategory(ApplicationCategoryType.CABLE_REPORT, [
  new TypeStructure(ApplicationType.CITY_STREET_AND_GREEN, [
    ApplicationSpecifier.CITY_STREET_AND_GREEN_CONSTRUCTION,
    ApplicationSpecifier.CITY_STREET_AND_GREEN_MAINTENANCE,
    ApplicationSpecifier.ASPHALT,
    ApplicationSpecifier.PAVEMENT,
    ApplicationSpecifier.BRIDGE
  ]),
  new TypeStructure(ApplicationType.WATER_AND_SEWAGE, [
    ApplicationSpecifier.STORM_DRAIN_CONSTRUCTION,
    ApplicationSpecifier.STORM_DRAIN_MAINTENANCE,
    ApplicationSpecifier.WATER_PIPE_CONSTRUCTION,
    ApplicationSpecifier.WATER_PIPE_MAINTENANCE,
    ApplicationSpecifier.WATER_PIPE_LEAK_REPAIR,
    ApplicationSpecifier.DRAIN_CONSTRUCTION,
    ApplicationSpecifier.DRAIN_MAINTENANCE,
    ApplicationSpecifier.DRAIN_LEAK_REPAIR
  ]),
  new TypeStructure(ApplicationType.HKL, [
    ApplicationSpecifier.HKL_STOP_WORK,
    ApplicationSpecifier.HKL_OTHER_WORK
  ]),
  new TypeStructure(ApplicationType.ELECTRIC_CABLE, [
    ApplicationSpecifier.CABLE_CONSTRUCTION,
    ApplicationSpecifier.CABLE_MAINTENANCE,
    ApplicationSpecifier.CABLE_REPAIR,
    ApplicationSpecifier.OUTDOOR_LIGHTING,
    ApplicationSpecifier.TRAFFIC_LIGHTS
  ]),
  new TypeStructure(ApplicationType.DISTRICT_HEATING, [
    ApplicationSpecifier.DISTRICT_HEATING_CONSTRUCTION,
    ApplicationSpecifier.DISTRICT_HEATING_MAINTENANCE,
    ApplicationSpecifier.DISTRICT_HEATING_REPAIR
  ]),
  new TypeStructure(ApplicationType.DISTRICT_COOLING, [
    ApplicationSpecifier.DISTRICT_COOLING_CONSTRUCTION,
    ApplicationSpecifier.DISTRICT_COOLING_MAINTENANCE,
    ApplicationSpecifier.DISTRICT_COOLING_REPAIR
  ]),
  new TypeStructure(ApplicationType.TELECOMMUNICATION, [
    ApplicationSpecifier.TELECOMMUNICATION_CONSTRUCTION,
    ApplicationSpecifier.TELECOMMUNICATION_MAINTENANCE,
    ApplicationSpecifier.TELECOMMUNICATION_REPAIR
  ]),
  new TypeStructure(ApplicationType.GAS, [
    ApplicationSpecifier.GAS_CONSTRUCTION,
    ApplicationSpecifier.GAS_MAINTENANCE,
    ApplicationSpecifier.GAS_REPAIR
  ]),
  new TypeStructure(ApplicationType.AD_PILLARS_AND_STOPS, [
    ApplicationSpecifier.AD_STOPS,
    ApplicationSpecifier.AD_BILLBOARDS_AND_PILLARS
  ]),
  new TypeStructure(ApplicationType.PROPERTY_MERGER, [
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
  new TypeStructure(ApplicationType.SOIL_INVESTIGATION, [
    ApplicationSpecifier.SOIL_INVESTIGATION_DRILLING,
    ApplicationSpecifier.SOIL_INVESTIGATION_PIPING,
    ApplicationSpecifier.SOIL_INVESTIGATION_TEST_HOLES,
    ApplicationSpecifier.SOIL_INVESTIGATION_OTHER
  ]),
  new TypeStructure(ApplicationType.JOINT_MUNICIPAL_INFRASTRUCTURE, [
    ApplicationSpecifier.JOINT_MUNICIPAL_INFRASTRUCTURE_CONSTRUCTION,
    ApplicationSpecifier.JOINT_MUNICIPAL_INFRASTRUCTURE_MAINTENANCE
  ]),
  new TypeStructure(ApplicationType.ABSORBING_SEWAGE_SYSTEM, [
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_CONSTRUCTION,
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_MAINTENANCE,
    ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM_REPAIR
  ]),
  new TypeStructure(ApplicationType.UNDERGROUND_CONSTRUCTION, [
    ApplicationSpecifier.UNDERGROUND_CONSTRUCTION_SPACE,
    ApplicationSpecifier.UNDERGROUND_CONSTRUCTION_STRUCTURE
  ]),
  new TypeStructure(ApplicationType.OTHER_CABLE_REPORT, [
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

export const applicationCategories: Array<ApplicationCategory> = [
  street,
  event,
  shortTermRental,
  cableReport
];
