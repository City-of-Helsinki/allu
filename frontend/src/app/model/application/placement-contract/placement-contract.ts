import {ApplicationType} from '../type/application-type';
import {ApplicationExtension} from '../type/application-extension';

export class PlacementContract extends ApplicationExtension {
  constructor(
    public propertyIdentificationNumber?: string,
    public additionalInfo?: string,
    public contractText?: string,
    public terms?: string,
    public rationale?: string
  ) {
    super(ApplicationType[ApplicationType.PLACEMENT_CONTRACT], terms);
  }
}
