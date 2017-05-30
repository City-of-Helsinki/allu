import {Customer} from './customer';
import {Contact} from './contact';
import {CustomerRoleType} from './customer-role-type';
import {Some} from '../../util/option';

export class CustomerWithContacts {
  constructor(
    public roleType?: CustomerRoleType,
    public customer: Customer = new Customer(),
    public contacts: Array<Contact> = []) {
  }

  get customerId(): number {
    return Some(this.customer).map(c => c.id).orElse(undefined);
  }

  get uiRoleType(): string {
    return CustomerRoleType[this.roleType];
  }
}
