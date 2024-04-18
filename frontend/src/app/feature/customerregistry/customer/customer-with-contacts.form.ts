import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {CustomerForm} from './customer.form';
import {Contact} from '../../../model/customer/contact';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../model/customer/customer-role-type';

export class CustomerWithContactsForm {
  constructor(
    public roleType?: string,
    public customer?: CustomerForm,
    public contacts?: Array<Contact>) {}

  public static fromCustomerWithContacts(cwc: CustomerWithContacts): CustomerWithContactsForm {
    return new CustomerWithContactsForm(
      CustomerRoleType[cwc.roleType],
      CustomerForm.fromCustomer(cwc.customer),
      cwc.contacts
    );
  }

  public static toCustomerWithContacts(form: CustomerWithContactsForm): CustomerWithContacts {
    return new CustomerWithContacts(
      form.roleType ? CustomerRoleType[form.roleType] : undefined,
      CustomerForm.toCustomer(form.customer),
      form.contacts
    );
  }

  public static initialForm(fb: UntypedFormBuilder, roleType: CustomerRoleType = CustomerRoleType.APPLICANT): UntypedFormGroup {
    return fb.group({
      roleType: [CustomerRoleType[roleType]],
      customer: CustomerForm.initialForm(fb),
      contacts: fb.array([])
    });
  }

  public static formName(roleType: CustomerRoleType): string {
    const rtName = CustomerRoleType[roleType];
    switch (roleType) {
      case CustomerRoleType.APPLICANT:
        return 'applicant';
      case CustomerRoleType.CONTRACTOR:
        return 'contractor';
      case CustomerRoleType.PROPERTY_DEVELOPER:
        return 'propertyDeveloper';
      case CustomerRoleType.REPRESENTATIVE:
        return 'representative';
      default:
        throw new Error('No form name for role type ' + rtName);
    }
  }
}
