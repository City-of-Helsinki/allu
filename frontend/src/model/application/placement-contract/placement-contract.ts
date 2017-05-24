import {Contact} from '../../customer/contact';
import {ApplicationType} from '../type/application-type';
import {ApplicationExtension} from '../type/application-extension';
import {Customer} from '../../customer/customer';

export class PlacementContract extends ApplicationExtension {
  constructor()
  constructor(
    specifiers: Array<string>,
    representative: Customer,
    contact: Contact,
    diaryNumber: string,
    additionalInfo: string,
    generalTerms: string,
    terms: string
  )
  constructor(
    public specifiers?: Array<string>,
    public representative?: Customer,
    public contact?: Contact,
    public diaryNumber?: string,
    public additionalInfo?: string,
    public generalTerms?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.PLACEMENT_CONTRACT], specifiers, terms);
  }

  get representativeContactList(): Array<Contact> {
    return this.contact ? [this.contact] : undefined;
  }
}
