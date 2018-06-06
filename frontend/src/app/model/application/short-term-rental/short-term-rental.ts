import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';

export class ShortTermRental extends ApplicationExtension {
  constructor(
    public description?: string,
    public commercial?: boolean,
    public billableSalesArea?: boolean,
    public terms?: string) {
    super(ApplicationType[ApplicationType.SHORT_TERM_RENTAL], terms);
  }
}
