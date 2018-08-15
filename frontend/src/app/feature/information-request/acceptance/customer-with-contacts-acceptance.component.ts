import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';

@Component({
  selector: 'customer-with-contacts-acceptance',
  templateUrl: './customer-with-contacts-acceptance.component.html',
  styleUrls: []
})
export class CustomerWithContactsAcceptanceComponent {
  @Input() oldCustomerWithContacts: CustomerWithContacts;
  @Input() newCustomerWithContacts: CustomerWithContacts;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
}
