import {Customer} from './customer';
import {Contact} from './contact';
import {CustomerRoleType} from './customer-role-type';
import {Some} from '../../util/option';
import {ArrayUtil} from '../../util/array-util';

export class CustomerWithContacts {
  constructor(
    public roleType?: CustomerRoleType,
    public customer?: Customer,
    public contacts: Array<Contact> = []) {
  }

  get customerId(): number {
    return Some(this.customer).map(c => c.id)
      .orElse(this.customerIdFromContacts());
  }

  get uiRoleType(): string {
    return CustomerRoleType[this.roleType];
  }

  private customerIdFromContacts(): number {
    return ArrayUtil.first(this.contacts.map(c => c.customerId).filter(c => !!c));
  }

}
