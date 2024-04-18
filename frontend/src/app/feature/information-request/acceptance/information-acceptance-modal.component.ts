import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import * as fromInformationRequestResult from '../reducers';
import * as fromInformationRequest from '@feature/information-request/reducers';
import {select, Store} from '@ngrx/store';
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
import {shrinkFadeInOut} from '@feature/common/animation/common-animations';
import {CloseRequest, LoadActiveRequest, LoadRequest} from '@feature/information-request/actions/information-request-actions';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {map, switchMap, take} from 'rxjs/operators';
import { InformationRequestStatus } from '@app/model/information-request/information-request-status';

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

const applicationIdPart = /[0-9]+/;

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
  form: UntypedFormGroup;
  updatedFields: InformationRequestFieldKey[];
  useCustomerForInvoicing$: Observable<CustomerRoleType>;
  hasLocationChanges: boolean;
  applicationTypeBillable: boolean;
  requestDataAvailable = false;
  showRequest = true;
  hideExisting = false;

  constructor(private dialogRef: MatDialogRef<InformationAcceptanceModalComponent>,
              private store: Store<fromRoot.State>,
              @Inject(MAT_DIALOG_DATA) public data: InformationAcceptanceData,
              private fb: UntypedFormBuilder,
              private resultService: InformationRequestResultService,
              private applicationStore: ApplicationStore,
              private location: Location,
              private router: Router) {
    this.form = this.fb.group({});
    this.readonly = true;
  }

  ngOnInit(): void {
    this.onApplicationChange(this.data.oldInfo);
    this.oldInfo = this.data.oldInfo;
    this.newInfo = this.data.newInfo;
    this.updatedFields = this.data.updatedFields;
    this.hasLocationChanges = ArrayUtil.anyMatch(this.updatedFields, LocationKeys);

    if (this.data.informationRequest) {
      this.requestDataAvailable = this.data.informationRequest.fields.length > 0;
      this.hideExisting = this.data.informationRequest.status === InformationRequestStatus.CLOSED;
    }

    // set initial values to the store
    const baseInfo = this.data.oldInfo || new Application();
    this.onApplicationChange(baseInfo);
    this.useCustomerForInvoicing$ = this.store.select(fromInformationRequestResult.useCustomerForInvoicing);
    this.applicationTypeBillable = [ApplicationType.CABLE_REPORT, ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS]
      .indexOf(this.oldInfo.type) < 0;
  }

  onSubmit(): void {
    this.store.pipe(
      select(fromInformationRequest.getActiveInformationRequest),
      take(1),
      map(request => request ? request.informationRequestId : undefined),
      switchMap(requestId => this.resultService.getResult(requestId))
    ).subscribe(result => this.dialogRef.close(result));
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
      .subscribe(application => {
        const path = this.location.path().replace(applicationIdPart, application.id.toString());
        this.router.navigate([path]);
        this.oldInfo = application;
        this.onApplicationChange(application);
        this.store.dispatch(new LoadActiveRequest());
      });
  }

  discardChanges(): void {
    this.store.dispatch(new CloseRequest(this.data.informationRequest.informationRequestId));
    this.dialogRef.close();
  }

  private onApplicationChange(application: Application): void {
    this.store.dispatch(new SetApplication(application));
    this.store.dispatch(new SetKindsWithSpecifiers(application.kindsWithSpecifiers));
    this.store.dispatch(new SetLocations(application.locations));
    this.readonly = this.isReadOnly(application);
  }

  private isReadOnly(application: Application): boolean {
    return this.data.readonly
      || !isBetween(application.status, ApplicationStatus.WAITING_INFORMATION, ApplicationStatus.WAITING_CONTRACT_APPROVAL);
  }
}
