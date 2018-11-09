import {AfterViewInit, ChangeDetectionStrategy, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import * as fromInformationRequestResult from '../reducers';
import {Store} from '@ngrx/store';
import {Application} from '@model/application/application';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {Observable, Subject} from 'rxjs/index';
import {distinctUntilChanged, map, skipUntil, startWith} from 'rxjs/internal/operators';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {SetApplication, SetCustomer, SetKindsWithSpecifiers} from '../actions/information-request-result-actions';
import * as fromRoot from '../../allu/reducers';
import {InformationRequestResultService} from '@feature/information-request/acceptance/result/information-request-result.service';

export interface InformationAcceptanceData {
  readonly?: boolean;
  informationRequestId?: number;
  oldInfo: Application;
  newInfo: Application;
  updatedFields: InformationRequestFieldKey[];
}

export const INFORMATION_ACCEPTANCE_MODAL_CONFIG: MatDialogConfig<InformationAcceptanceData> = {
  width: '80vw',
  disableClose: true,
  autoFocus: false
};

@Component({
  selector: 'information-acceptance-modal',
  templateUrl: './information-acceptance-modal.component.html',
  styleUrls: ['./information-acceptance-modal.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
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
              private fb: FormBuilder,
              private resultService: InformationRequestResultService) {
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
    this.resultService.getResult(this.data.informationRequestId)
      .subscribe(result => this.dialogRef.close(result));
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
