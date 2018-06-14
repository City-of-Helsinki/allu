import {CustomerWithContacts} from '../customer/customer-with-contacts';
import {Customer} from '../customer/customer';

export class ClientApplicationData {
  constructor(
    public customer?: CustomerWithContacts,
    public invoicingCustomer?: Customer,
    public clientApplicationKind?: string
  ) {}
}
