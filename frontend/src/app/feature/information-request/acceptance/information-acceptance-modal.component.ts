import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup} from '@angular/forms';
import * as fromInformationRequestResult from '../reducers';
import {Store} from '@ngrx/store';
import {Application} from '@model/application/application';
import {InformationRequestFieldKey, LocationKeys} from '@model/information-request/information-request-field-key';
import {Observable} from 'rxjs';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {SetApplication, SetKindsWithSpecifiers, SetLocations} from '../actions/information-request-result-actions';
import * as fromRoot from '../../allu/reducers';
import {InformationRequestResultService} from '@feature/information-request/acceptance/result/information-request-result.service';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStatus, isBetween} from '@model/application/application-status';
import {ArrayUtil} from '@util/array-util';
import {ApplicationType} from '@model/application/type/application-type';
import {InformationRequest} from '@model/information-request/information-request';
import {Some} from '@util/option';
import {shrinkFadeInOut} from '@feature/common/animation/common-animations';
import {CloseRequest} from '@feature/information-request/actions/information-request-actions';

export interface InformationAcceptanceData {
  readonly?: boolean;
  informationRequest?: InformationRequest;
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
  animations: [shrinkFadeInOut]
})
export class InformationAcceptanceModalComponent implements OnInit {
  readonly: boolean;
  oldInfo: Application;
  newInfo: Application;
  form: FormGroup;
  updatedFields: InformationRequestFieldKey[];
  useCustomerForInvoicing$: Observable<CustomerRoleType>;
  hasLocationChanges: boolean;
  applicationTypeBillable: boolean;
  requestDataAvailable = false;
  showRequest = true;

  constructor(private dialogRef: MatDialogRef<InformationAcceptanceModalComponent>,
              private store: Store<fromRoot.State>,
              @Inject(MAT_DIALOG_DATA) public data: InformationAcceptanceData,
              private fb: FormBuilder,
              private resultService: InformationRequestResultService,
              private applicationStore: ApplicationStore) {
    this.form = this.fb.group({});
    this.readonly = data.readonly;
  }

  ngOnInit(): void {
    this.oldInfo = this.data.oldInfo;
    this.newInfo = this.data.newInfo;
    this.updatedFields = this.data.updatedFields;
    this.hasLocationChanges = ArrayUtil.anyMatch(this.updatedFields, LocationKeys);
    this.requestDataAvailable = this.data.informationRequest && this.data.informationRequest.fields.length > 0;

    // set initial values to the store
    const baseInfo = this.data.oldInfo || new Application();
    this.onApplicationChange(baseInfo);
    this.useCustomerForInvoicing$ = this.store.select(fromInformationRequestResult.useCustomerForInvoicing);
    this.applicationTypeBillable = [ApplicationType.CABLE_REPORT, ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS]
      .indexOf(this.oldInfo.type) < 0;
  }

  onSubmit(): void {
    const requestId = Some(this.data.informationRequest).map(request => request.informationRequestId).orElse(undefined);
    this.resultService.getResult(requestId)
      .subscribe(result => this.dialogRef.close(result));
  }

  cancel(): void {
    this.dialogRef.close();
  }

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.oldInfo.id, ApplicationStatus.HANDLING)
      .subscribe(application => this.onApplicationChange(application));
  }

  replace(): void {
    this.applicationStore.replace()
      .subscribe(application => this.onApplicationChange(application));
  }

  discardChanges(): void {
    this.store.dispatch(new CloseRequest(this.data.informationRequest.informationRequestId));
    this.dialogRef.close();
  }

  private onApplicationChange(application: Application): void {
    this.store.dispatch(new SetApplication(application));
    this.store.dispatch(new SetKindsWithSpecifiers(application.kindsWithSpecifiers));
    this.store.dispatch(new SetLocations(application.locations));
    this.readonly = this.data.readonly
      || !isBetween(application.status, ApplicationStatus.WAITING_INFORMATION, ApplicationStatus.WAITING_CONTRACT_APPROVAL);
  }
}
