import {commonKinds} from './application-kind';
import {Some} from '../../../util/option';
import {KindsWithSpecifiers} from './application-specifier';
import {ObjectUtil} from '../../../util/object.util';
import {ArrayUtil} from '../../../util/array-util';

export enum ApplicationType {
  EXCAVATION_ANNOUNCEMENT = 'EXCAVATION_ANNOUNCEMENT', // Kaivuilmoitus
  AREA_RENTAL = 'AREA_RENTAL', // Aluevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS = 'TEMPORARY_TRAFFIC_ARRANGEMENTS', // Väliaikainen liikennejärjestely
  CABLE_REPORT = 'CABLE_REPORT', // Johtoselvitykset
  PLACEMENT_CONTRACT = 'PLACEMENT_CONTRACT', // Sijoitusopimukset
  EVENT = 'EVENT', // Tapahtuma
  SHORT_TERM_RENTAL = 'SHORT_TERM_RENTAL', // Lyhytaikainen maanvuokraus
  NOTE = 'NOTE' // Muistiinpano
}

export interface ApplicationTypeTree {
  [type: string]: KindsWithSpecifiers;
}

export const applicationTypeTree: ApplicationTypeTree = {
  EXCAVATION_ANNOUNCEMENT: {
    ...commonKinds
  },
  AREA_RENTAL: {
    PROPERTY_RENOVATION: [],
    CONTAINER_BARRACK: [],
    PHOTO_SHOOTING: [],
    SNOW_WORK: [],
    RELOCATION: [],
    LIFTING: [],
    NEW_BUILDING_CONSTRUCTION: [],
    ROLL_OFF: [],
    OTHER: []
  },
  TEMPORARY_TRAFFIC_ARRANGEMENTS: {
    PUBLIC_EVENT: [],
    OTHER: []
  },
  CABLE_REPORT: {
    ...commonKinds
  },
  PLACEMENT_CONTRACT: {
    ...commonKinds
  },
  EVENT: {
    PROMOTION: [],
    OUTDOOREVENT: []
  },
  SHORT_TERM_RENTAL: {
    BRIDGE_BANNER: [],
    BENJI: [],
    PROMOTION_OR_SALES: [],
    URBAN_FARMING: [],
    KESKUSKATU_SALES: [],
    SUMMER_THEATER: [],
    DOG_TRAINING_FIELD: [],
    DOG_TRAINING_EVENT: [],
    SMALL_ART_AND_CULTURE: [],
    SEASON_SALE: [],
    CIRCUS: [],
    ART: [],
    STORAGE_AREA: [],
    OTHER: []
  },
  NOTE: {
    CHRISTMAS_TREE_SALES_AREA: [],
    CITY_CYCLING_AREA: [],
    AGILE_KIOSK_AREA: [],
    STATEMENT: [],
    SNOW_HEAP_AREA: [],
    SNOW_GATHER_AREA: [],
    OTHER_SUBVISION_OF_STATE_AREA: [],
    MILITARY_EXCERCISE: [],
    WINTER_PARKING: [],
    REPAVING: [],
    ELECTION_ADD_STAND: [],
    OTHER: []
  }
};

export function hasSingleKind(typeName: string): boolean {
  return !hasMultipleKinds(typeName);
}

export function hasMultipleKinds(typeName: string): boolean {
  return [
    ApplicationType.CABLE_REPORT,
    ApplicationType.EXCAVATION_ANNOUNCEMENT,
    ApplicationType.PLACEMENT_CONTRACT
    ].indexOf(ApplicationType[typeName]) >= 0;
}

export function getAvailableKinds(type: string): string[] {
  return Some(applicationTypeTree[type])
    .map(typeTree => Object.keys(typeTree))
    .map(kinds => kinds.sort(ArrayUtil.naturalSortTranslated(['application.kind'], (kind: string) => kind)))
    .orElse([]);
}

export function getAvailableSpecifiers(applicationType: string, kinds: Array<string>): KindsWithSpecifiers {
  return Some(applicationTypeTree[applicationType])
    .map(allKinds => ObjectUtil.filter(allKinds, (fieldName => kinds.some(kind => kind === fieldName))))
    .orElse({});
}

