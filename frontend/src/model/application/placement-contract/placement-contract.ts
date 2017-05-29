import {Contact} from '../../customer/contact';
import {ApplicationType} from '../type/application-type';
import {ApplicationExtension} from '../type/application-extension';
import {Customer} from '../../customer/customer';

export class PlacementContract extends ApplicationExtension {
  constructor(
    public specifiers?: Array<string>,
    public diaryNumber?: string,
    public additionalInfo?: string,
    public generalTerms?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.PLACEMENT_CONTRACT], specifiers, terms);
  }
}
