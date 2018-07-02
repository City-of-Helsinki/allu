import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import * as fromInformationRequest from '../reducers';
import {Store} from '@ngrx/store';
import {InformationRequestResult} from '../information-request-result';
import {Application} from '../../../model/application/application';
import {InformationRequestFieldKey} from '../../../model/information-request/information-request-field-key';
import {combineLatest, Observable, Subject} from 'rxjs/index';
import {map, skipUntil, startWith, take} from 'rxjs/internal/operators';
import {CustomerRoleType} from '../../../model/customer/customer-role-type';
import {ArrayUtil} from '../../../util/array-util';
import {SetApplication, SetCustomer} from '../actions/information-request-result-actions';
import * as fromRoot from '../../allu/reducers';
import {CodeSetCodeMap} from '../../../model/codeset/codeset';
import {InformationAcceptanceModalEvents} from './information-acceptance-modal-events';

export interface InformationAcceptanceData {
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

  form: FormGroup;
  updatedFields: string[];
  submitDisabled: Observable<boolean>;
  countryCodes$: Observable<CodeSetCodeMap>;

  private childrenLoaded$ = new Subject<boolean>();

  constructor(private dialogRef: MatDialogRef<InformationAcceptanceModalComponent>,
              private store: Store<fromRoot.State>,
              @Inject(MAT_DIALOG_DATA) public data: InformationAcceptanceData,
              private fb: FormBuilder,
              private modalState: InformationAcceptanceModalEvents) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.updatedFields = this.data.updatedFields.map(field => InformationRequestFieldKey[field]);

    // set initial values to the store
    const baseInfo = this.data.oldInfo || new Application();
    this.store.dispatch(new SetApplication(baseInfo));
    this.store.dispatch(new SetCustomer(baseInfo.applicant.customer));

    // Need to wait until viewChildren are loaded so they wont emit
    // form changes before component tree has been stabilized
    this.submitDisabled = this.form.statusChanges.pipe(
      skipUntil(this.childrenLoaded$),
      map(status => status !== 'VALID'),
      startWith(true)
    );

    this.countryCodes$ = this.store.select(fromRoot.getCodeSetCodeMap('Country'));
  }

  ngAfterViewInit(): void {
    this.childrenLoaded$.next(true);
  }

  onSubmit(): void {
    combineLatest(
      this.store.select(fromInformationRequest.getResultApplication),
      this.store.select(fromInformationRequest.getResultCustomerWithContacts),
      this.store.select(fromInformationRequest.getResultKindsWithSpecifiers)
    ).pipe(take(1))
      .subscribe(([app, customerWithContacts, kindsWithSpecifiers]) => {
        app.customersWithContacts = ArrayUtil.createOrReplace(
          app.customersWithContacts,
          customerWithContacts,
          cwc => cwc.roleType === CustomerRoleType.APPLICANT);
        app.kindsWithSpecifiers = kindsWithSpecifiers;
        this.dialogRef.close(new InformationRequestResult(this.data.informationRequestId, app));
      });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
