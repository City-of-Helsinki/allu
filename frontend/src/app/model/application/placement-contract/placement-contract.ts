import {ApplicationType} from '../type/application-type';
import {ApplicationExtension} from '../type/application-extension';

export class PlacementContract extends ApplicationExtension {
  constructor(
    public diaryNumber?: string,
    public additionalInfo?: string,
    public generalTerms?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.PLACEMENT_CONTRACT], terms);
  }
}
