import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';

@Component({
  selector: 'customer-with-contacts-acceptance',
  templateUrl: './customer-with-contacts-acceptance.component.html',
  styleUrls: ['./customer-with-contacts-acceptance.component.scss']
})
export class CustomerWithContactsAcceptanceComponent {
  @Input() oldCustomerWithContacts: CustomerWithContacts;
  @Input() newCustomerWithContacts: CustomerWithContacts;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;
  @Input() canBeInvoiceRecipient = false;
  @Input() hideExisting = false;
}
