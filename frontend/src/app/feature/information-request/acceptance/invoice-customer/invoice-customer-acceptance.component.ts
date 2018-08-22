import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {FormBuilder} from '@angular/forms';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {Customer} from '@model/customer/customer';
import {SetInvoicingCustomer} from '@feature/information-request/actions/information-request-result-actions';

@Component({
  selector: 'invoice-customer-acceptance',
  templateUrl: './invoice-customer-acceptance.component.html',
  styleUrls: []
})
export class InvoiceCustomerAcceptanceComponent extends CustomerAcceptanceComponent {

  protected formName = 'invoiceCustomer';

  constructor(fb: FormBuilder, store: Store<fromRoot.State>) {
    super(fb, store);
  }

  customerChanges(customer: Customer): void {
    this.store.dispatch(new SetInvoicingCustomer(customer));
  }
}
