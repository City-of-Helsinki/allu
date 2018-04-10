import {ApplicationType} from '../type/application-type';
import {ApplicationExtension} from '../type/application-extension';

export class PlacementContract extends ApplicationExtension {
  constructor(
    public identificationNumber?: string,
    public propertyIdentificationNumber?: string,
    public additionalInfo?: string,
    public contractText?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.PLACEMENT_CONTRACT], terms);
  }
}
