import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import * as fromInformationRequestResult from '../reducers';
import {Store} from '@ngrx/store';
import {InformationRequestResult} from '../information-request-result';
import {Application} from '@model/application/application';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {combineLatest, Observable, Subject} from 'rxjs/index';
import {distinctUntilChanged, map, skipUntil, startWith, take} from 'rxjs/internal/operators';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {ArrayUtil} from '@util/array-util';
import {SetApplication, SetCustomer, SetKindsWithSpecifiers} from '../actions/information-request-result-actions';
import * as fromRoot from '../../allu/reducers';

export interface InformationAcceptanceData {
  readonly?: boolean;
  informationRequestId?: number;
  oldInfo: Application;
  newInfo: Application;
  updatedFields: InformationRequestFieldKey[];
}

export const INFORMATION_ACCEPTANCE_MODAL_CONFIG: MatDialogConfig<InformationAcceptanceData> = {
  width: '80vw',
  disableClose: true
};

@Component({
  selector: 'information-acceptance-modal',
  templateUrl: './information-acceptance-modal.component.html',
  styleUrls: ['./information-acceptance-modal.component.scss']
})
export class InformationAcceptanceModalComponent implements OnInit, AfterViewInit {

  oldInfo: Application;
  newInfo: Application;
  form: FormGroup;
  updatedFields: string[];
  submitDisabled: Observable<boolean>;
  readonly: boolean;
  useCustomerForInvoicing$: Observable<CustomerRoleType>;

  private childrenLoaded$ = new Subject<boolean>();

  constructor(private dialogRef: MatDialogRef<InformationAcceptanceModalComponent>,
              private store: Store<fromRoot.State>,
              @Inject(MAT_DIALOG_DATA) public data: InformationAcceptanceData,
              private fb: FormBuilder) {
    this.form = this.fb.group({});
    this.readonly = data.readonly;

    // Need to wait until viewChildren are loaded so they wont emit
    // form changes before component tree has been stabilized
    this.submitDisabled = this.form.statusChanges.pipe(
      skipUntil(this.childrenLoaded$),
      map(status => status !== 'VALID'),
      startWith(true),
      distinctUntilChanged()
    );
  }

  ngOnInit(): void {
    this.oldInfo = this.data.oldInfo;
    this.newInfo = this.data.newInfo;
    this.updatedFields = this.data.updatedFields.map(field => InformationRequestFieldKey[field]);

    // set initial values to the store
    const baseInfo = this.data.oldInfo || new Application();
    this.store.dispatch(new SetApplication(baseInfo));
    this.store.dispatch(new SetCustomer(baseInfo.applicant.customer));
    this.store.dispatch(new SetKindsWithSpecifiers(baseInfo.kindsWithSpecifiers));
    this.useCustomerForInvoicing$ = this.store.select(fromInformationRequestResult.useCustomerForInvoicing);
  }

  ngAfterViewInit(): void {
    this.childrenLoaded$.next(true);
  }

  onSubmit(): void {
    combineLatest(
      this.store.select(fromInformationRequestResult.getResultApplication),
      this.store.select(fromInformationRequestResult.getResultCustomerWithContacts),
      this.store.select(fromInformationRequestResult.getResultKindsWithSpecifiers),
      this.store.select(fromInformationRequestResult.getResultInvoicingCustomer),
      this.store.select(fromInformationRequestResult.useCustomerForInvoicing)
    ).pipe(take(1))
      .subscribe(([app, customerWithContacts, kindsWithSpecifiers, invoicingCustomer, useCustomerForInvoicing]) => {
        app.customersWithContacts = ArrayUtil.createOrReplace(
          app.customersWithContacts,
          customerWithContacts,
          cwc => cwc.roleType === CustomerRoleType.APPLICANT);
        app.kindsWithSpecifiers = kindsWithSpecifiers;
        const result = new InformationRequestResult(this.data.informationRequestId, app, invoicingCustomer, useCustomerForInvoicing);
        this.dialogRef.close(result);
      });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
