import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {FormBuilder} from '@angular/forms';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {Customer} from '@model/customer/customer';
import {SetInvoicingCustomer} from '@feature/information-request/actions/information-request-result-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {Observable} from 'rxjs/index';

@Component({
  selector: 'invoice-customer-acceptance',
  templateUrl: './invoice-customer-acceptance.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceCustomerAcceptanceComponent extends CustomerAcceptanceComponent {

  protected formName = 'invoiceCustomer';
  protected actionTargetType = ActionTargetType.InvoicingCustomer;

  constructor(fb: FormBuilder, store: Store<fromRoot.State>) {
    super(fb, store);
  }

  customerChanges(customer: Customer): void {
    this.store.dispatch(new SetInvoicingCustomer(customer));
  }

  getMatchingCustomers(): Observable<Customer[]> {
    return this.store.select(fromCustomerSearch.getMatchingInvoicingCustomers);
  }

  getLoading(): Observable<boolean> {
    return this.store.select(fromCustomerSearch.getInvoicingCustomersLoading);
  }
}
