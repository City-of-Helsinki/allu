import {Component} from '@angular/core';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {FormBuilder} from '@angular/forms';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {Customer} from '@model/customer/customer';
import {SetCustomer} from '@feature/information-request/actions/information-request-result-actions';

@Component({
  selector: 'applicant-acceptance',
  templateUrl: './applicant-acceptance.component.html',
  styleUrls: []
})
export class ApplicantAcceptanceComponent extends CustomerAcceptanceComponent {

  protected formName = 'applicant';

  constructor(fb: FormBuilder, store: Store<fromRoot.State>) {
    super(fb, store);
  }

  customerChanges(customer: Customer): void {
    this.store.dispatch(new SetCustomer(customer));
  }
}
