import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import * as fromInformationRequestResult from '../reducers';
import {Store} from '@ngrx/store';
import {Application} from '@model/application/application';
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {Observable, Subject} from 'rxjs/index';
import {distinctUntilChanged, map} from 'rxjs/internal/operators';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {SetApplication, SetCustomer, SetKindsWithSpecifiers, SetLocations} from '../actions/information-request-result-actions';
import * as fromRoot from '../../allu/reducers';
import {InformationRequestResultService} from '@feature/information-request/acceptance/result/information-request-result.service';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStatus, isBefore} from '@model/application/application-status';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

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
  styleUrls: ['./information-acceptance-modal.component.scss']
})
export class InformationAcceptanceModalComponent implements OnInit, AfterViewInit {
  readonly: boolean;
  oldInfo: Application;
  newInfo: Application;
  form: FormGroup;
  updatedFields: InformationRequestFieldKey[];
  submitDisabled: Observable<boolean>;
  useCustomerForInvoicing$: Observable<CustomerRoleType>;

  private childrenLoaded$ = new Subject<boolean>();

  constructor(private dialogRef: MatDialogRef<InformationAcceptanceModalComponent>,
              private store: Store<fromRoot.State>,
              @Inject(MAT_DIALOG_DATA) public data: InformationAcceptanceData,
              private fb: FormBuilder,
              private resultService: InformationRequestResultService,
              private applicationStore: ApplicationStore) {
    this.form = this.fb.group({});
    this.readonly = data.readonly;

    // Need to wait until viewChildren are loaded so they wont emit
    // form changes before component tree has been stabilized
    this.submitDisabled = this.form.statusChanges.pipe(
      map(status => status !== 'VALID'),
      distinctUntilChanged()
    );
  }

  ngOnInit(): void {
    this.oldInfo = this.data.oldInfo;
    this.newInfo = this.data.newInfo;
    this.updatedFields = this.data.updatedFields;

    // set initial values to the store
    const baseInfo = this.data.oldInfo || new Application();
    this.onApplicationChange(baseInfo);
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

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.oldInfo.id, ApplicationStatus.HANDLING)
      .subscribe(application => this.onApplicationChange(application));
  }

  private onApplicationChange(application: Application): void {
    this.store.dispatch(new SetApplication(application));
    this.store.dispatch(new SetKindsWithSpecifiers(application.kindsWithSpecifiers));
    this.store.dispatch(new SetLocations(application.locations));
    this.readonly = this.data.readonly || isBefore(application.status, ApplicationStatus.INFORMATION_RECEIVED);
  }
}
