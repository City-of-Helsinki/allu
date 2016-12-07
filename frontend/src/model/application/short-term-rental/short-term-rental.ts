import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';

export class ShortTermRental extends ApplicationExtension {
  public applicationType = ApplicationType[ApplicationType.SHORT_TERM_RENTAL];

  constructor()
  constructor(
    description: string,
    commercial?: boolean,
    largeSalesArea?: boolean)
  constructor(
    public description?: string,
    public commercial?: boolean,
    public largeSalesArea?: boolean) {
    super();
  }
}
