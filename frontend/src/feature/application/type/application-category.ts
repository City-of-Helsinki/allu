import {ApplicationType} from '../../../model/application/type/application-type';

export enum ApplicationCategoryType {
  STREET,
  EVENT,
  SHORT_TERM_RENTAL
}

export class ApplicationCategory {
  constructor(public categoryType: ApplicationCategoryType, private applicationTypes: Array<ApplicationType>) {}

  public containsType(applicationType: ApplicationType): boolean {
    return this.applicationTypes.indexOf(applicationType) >= 0;
  }

  get categoryTypeName() {
    return ApplicationCategoryType[this.categoryType];
  }

  get applicationTypeNames() {
    return this.applicationTypes.map(type => ApplicationType[type]);
  }
}

export const street = new ApplicationCategory(ApplicationCategoryType.STREET, [
  ApplicationType.EXCAVATION_ANNOUNCEMENT,
  ApplicationType.AREA_RENTAL,
  ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS
]);

export const event = new ApplicationCategory(ApplicationCategoryType.EVENT, [
  ApplicationType.PROMOTION,
  ApplicationType.OUTDOOREVENT,
  ApplicationType.ELECTION
]);

export const shortTermRental = new ApplicationCategory(ApplicationCategoryType.SHORT_TERM_RENTAL, [
  ApplicationType.BRIDGE_BANNER,
  ApplicationType.BENJI,
  ApplicationType.PROMOTION_OR_SALES,
  ApplicationType.URBAN_FARMING,
  ApplicationType.MAIN_STREET_SALES,
  ApplicationType.SUMMER_THEATER,
  ApplicationType.DOG_TRAINING_FIELD,
  ApplicationType.DOG_TRAINING_EVENT,
  ApplicationType.CARGO_CONTAINER,
  ApplicationType.SMALL_ART_AND_CULTURE,
  ApplicationType.SEASON_SALE,
  ApplicationType.CIRCUS,
  ApplicationType.ART,
  ApplicationType.STORAGE_AREA,
  ApplicationType.OTHER_SHORT_TERM_RENTAL
]);

export const applicationCategories: Array<ApplicationCategory> = [
  street,
  event,
  shortTermRental
];
