import {ApplicationTypeData} from '../type/application-type-data';
import {ApplicationCategoryType} from '../../../feature/application/type/application-category';

export class ShortTermRental extends ApplicationTypeData {
  public applicationCategory = ApplicationCategoryType[ApplicationCategoryType.SHORT_TERM_RENTAL];

  constructor()
  constructor(
    type: string,
    description: string,
    commercial?: boolean,
    largeSalesArea?: boolean)
  constructor(
    public type?: string,
    public description?: string,
    public commercial?: boolean,
    public largeSalesArea?: boolean) {
    super();
  }
}
