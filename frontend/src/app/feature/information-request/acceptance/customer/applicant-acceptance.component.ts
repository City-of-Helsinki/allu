import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CustomerAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-acceptance.component';
import {FormBuilder, FormControl} from '@angular/forms';
import * as fromRoot from '@feature/allu/reducers';
import {select, Store} from '@ngrx/store';
import {Customer} from '@model/customer/customer';
import {SetCustomer, UseCustomerForInvoicing} from '@feature/information-request/actions/information-request-result-actions';
import {map, takeUntil} from 'rxjs/internal/operators';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {Observable} from 'rxjs/index';
import {NumberUtil} from '@util/number.util';
import {LoadByCustomer, LoadByCustomerSuccess} from '@feature/customerregistry/actions/contact-search-actions';
import {MatDialog} from '@angular/material';

@Component({
  selector: 'applicant-acceptance',
  templateUrl: './applicant-acceptance.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicantAcceptanceComponent extends CustomerAcceptanceComponent {
  protected formName = 'applicant';
  protected actionTargetType = ActionTargetType.Applicant;
  private useForInvoicingCtrl: FormControl;

  constructor(fb: FormBuilder, store: Store<fromRoot.State>, dialog: MatDialog) {
    super(fb, store, dialog);
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

  getMatchingCustomers(): Observable<Customer[]> {
    return this.store.pipe(select(fromCustomerSearch.getMatchingApplicants));
  }

  getLoading(): Observable<boolean> {
    return this.store.pipe(select(fromCustomerSearch.getApplicantsLoading));
  }


  selectReferenceCustomer(customer?: Customer): void {
    super.selectReferenceCustomer(customer);

    if (NumberUtil.isExisting(customer)) {
      this.store.dispatch(new LoadByCustomer(ActionTargetType.Applicant, customer.id));
    } else {
      this.store.dispatch(new LoadByCustomerSuccess(ActionTargetType.Applicant, []));
    }
  }
}
