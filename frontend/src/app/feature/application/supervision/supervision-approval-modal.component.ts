import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {ComplexValidator} from '@util/complex-validator';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ApplicationType} from '@model/application/type/application-type';
import {ReportedDateType} from '@feature/application/date-reporting/date-reporting-modal.component';
import {map, startWith, take, takeUntil} from 'rxjs/internal/operators';
import {combineLatest, Observable, of, Subject} from 'rxjs/index';
import {Application} from '@model/application/application';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {ApplicationStatus} from '@model/application/application-status';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';

export const SUPERVISION_APPROVAL_MODAL_CONFIG = {width: '600px', data: {}};

export enum SupervisionApprovalResolutionType {
  APPROVE = 'APPROVE',
  REJECT = 'REJECT',
}

export interface SupervisionApprovalModalData {
  resolutionType: SupervisionApprovalResolutionType;
  taskType: SupervisionTaskType;
  application: Application;
  reportedDate?: Date;
  comparedDate?: Date;
}

export interface SupervisionApprovalResult {
  result: string;
  newSupervisionDate?: Date;
  reportedDate?: Date;
  statusChange?: ApplicationStatus;
}

const taskTypeToReportedDateType = {
  OPERATIONAL_CONDITION: ReportedDateType.WINTER_TIME_OPERATION,
  FINAL_SUPERVISION: ReportedDateType.WORK_FINISHED
};

const taskTypeToApplicationStatus = {
  OPERATIONAL_CONDITION: ApplicationStatus.OPERATIONAL_CONDITION,
  FINAL_SUPERVISION: ApplicationStatus.FINISHED
};

@Component({
  selector: 'supervision-approval-modal',
  templateUrl: './supervision-approval-modal.component.html',
  styleUrls: []
})
export class SupervisionApprovalModalComponent implements OnInit, OnDestroy {
  resolutionType: SupervisionApprovalResolutionType;
  form: FormGroup;
  showNewSupervisionDate = false;
  showToDecisionMaking = false;
  showExcavationExtras = false;
  reportedDateType: ReportedDateType;
  application: Application;

  private destroy = new Subject<boolean>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: SupervisionApprovalModalData,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SupervisionApprovalModalComponent>,
    private store: Store<fromRoot.State>,
    private configurationHelper: ConfigurationHelperService) {
  }

  ngOnInit(): void {
    this.application = this.data.application;
    this.resolutionType = this.data.resolutionType || SupervisionApprovalResolutionType.APPROVE;
    this.showExcavationExtras = this.needExcavationExtras(this.data.application.type, this.data.taskType, this.resolutionType);
    this.initForm();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
  }

  confirm() {
    this.dialogRef.close(this.createConfirmation());
  }

  toDecisionMaking() {
    const confirmation = this.createConfirmation();
    this.dialogRef.close({
      ...confirmation,
      statusChange: ApplicationStatus.DECISIONMAKING
    });
  }

  cancel() {
    this.dialogRef.close();
  }

  private initForm(): void {
    this.form = this.fb.group({
      result: [undefined, Validators.required]
    });

    if (SupervisionApprovalResolutionType.REJECT === this.resolutionType) {
      this.initReject();
    }

    if (this.showExcavationExtras) {
      this.initReportedDate();
    }
  }

  private initReject(): void {
    const newDateDefault = TimeUtil.datePlusWeeks(new Date(), 2);
    this.form.addControl('newSupervisionDate', this.fb.control(newDateDefault, [Validators.required, ComplexValidator.inThePast]));
    this.showNewSupervisionDate = true;
  }

  private initReportedDate(): void {
    const reportedDateCtrl = this.fb.control(this.data.reportedDate, Validators.required);
    this.form.addControl('reportedDate', reportedDateCtrl);
    this.reportedDateType = taskTypeToReportedDateType[this.data.taskType];

    reportedDateCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      startWith(this.data.reportedDate)
    ).subscribe(date => this.onReportedDateChange(date));
  }

  private onReportedDateChange(date: Date): void {
    if (this.application.invoicingChanged) {
      this.showToDecisionMaking = true;
    } else {
      this.requiresDecision(date).subscribe(requires => this.showToDecisionMaking = requires);
    }
  }

  private requiresDecision(date: Date): Observable<boolean> {
    if (ReportedDateType.WINTER_TIME_OPERATION === this.reportedDateType) {
      return of(this.operationalConditionRequiresDecision(date));
    } else if (ReportedDateType.WORK_FINISHED === this.reportedDateType) {
      return this.workFinishedRequiresDecision(date);
    } else {
      return of(false);
    }
  }

  private operationalConditionRequiresDecision(date: Date): boolean {
    return !TimeUtil.isSame(date, this.data.comparedDate, 'day');
  }

  private workFinishedRequiresDecision(date: Date): Observable<boolean> {
    const excavation = <ExcavationAnnouncement> this.application.extension;
    const dateChange = !TimeUtil.isSame(date, this.data.comparedDate, 'day');
    const isWinterTimeOperation = !!excavation.winterTimeOperation;

    return this.configurationHelper.inWinterTime(date).pipe(
        take(1),
        map(finishesInWinter => dateChange && !(isWinterTimeOperation && finishesInWinter))
    );
  }

  private createConfirmation(): SupervisionApprovalResult {
    const formValue = this.form.value;
    return {
      result: formValue.result,
      newSupervisionDate: formValue.newSupervisionDate,
      reportedDate: formValue.reportedDate,
      statusChange: this.getStatusChange()
    };
  }

  private needExcavationExtras(applicationType: ApplicationType, taskType: SupervisionTaskType,
                               resolutionType: SupervisionApprovalResolutionType): boolean {
    const validApplicationType = ApplicationType.EXCAVATION_ANNOUNCEMENT === applicationType;
    const validTaskType = [SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskType.FINAL_SUPERVISION].indexOf(taskType) >= 0;
    const validResolutionType = resolutionType === SupervisionApprovalResolutionType.APPROVE;
    return validApplicationType && validTaskType && validResolutionType;
  }

  private getStatusChange(): ApplicationStatus {
    if (this.showExcavationExtras) {
      return taskTypeToApplicationStatus[this.data.taskType];
    } else {
      return undefined;
    }
  }
}
