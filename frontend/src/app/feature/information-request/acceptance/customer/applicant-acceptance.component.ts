import {Component} from '@angular/core';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {FormBuilder, FormControl} from '@angular/forms';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {Customer} from '@model/customer/customer';
import {SetCustomer, UseCustomerForInvoicing} from '@feature/information-request/actions/information-request-result-actions';
import {map, takeUntil} from 'rxjs/internal/operators';
import {CustomerRoleType} from '@model/customer/customer-role-type';

@Component({
  selector: 'applicant-acceptance',
  templateUrl: './applicant-acceptance.component.html',
  styleUrls: []
})
export class ApplicantAcceptanceComponent extends CustomerAcceptanceComponent {
  protected formName = 'applicant';
  private useForInvoicingCtrl: FormControl;

  constructor(fb: FormBuilder, store: Store<fromRoot.State>) {
    super(fb, store);
  }

  init(): void {
    this.useForInvoicingCtrl = this.fb.control(false);
    this.searchForm.addControl('useForInvoicing', this.useForInvoicingCtrl);

    this.useForInvoicingCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      map(useCustomerForInvoicing => useCustomerForInvoicing ? CustomerRoleType.APPLICANT : undefined)
    ).subscribe(roleType => {
      this.store.dispatch(new UseCustomerForInvoicing(roleType));
    });
  }

  customerChanges(customer: Customer): void {
    this.store.dispatch(new SetCustomer(customer));
  }
}
