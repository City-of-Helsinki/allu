import {Component, Inject, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ReportedDateType} from '@feature/application/date-reporting/date-reporting-modal.component';
import {startWith, takeUntil} from 'rxjs/internal/operators';
import {ApplicationStatus} from '@model/application/application-status';
import {
  SupervisionApprovalModalComponent,
  SupervisionApprovalModalData,
  SupervisionApprovalResolutionType,
  SupervisionApprovalResult
} from '@feature/application/supervision/supervision-approval-modal.component';
import {ComplexValidator} from '@util/complex-validator';

export interface AreaRentalSupervisionApprovalModalData extends SupervisionApprovalModalData {
  reportedDate?: Date;
  comparedDate?: Date;
  minDate?: Date;
  lastAreaEndDate?: Date;
}

const taskTypeToReportedDateType = {
  FINAL_SUPERVISION: ReportedDateType.WORK_FINISHED
};

const taskTypeToApplicationStatus = {
  FINAL_SUPERVISION: ApplicationStatus.FINISHED
};

@Component({
  selector: 'area-rental-supervision-approval-modal',
  templateUrl: './area-rental-supervision-approval-modal.component.html',
  styleUrls: []
})
export class AreaRentalSupervisionApprovalModalComponent extends SupervisionApprovalModalComponent implements OnInit {
  showToDecisionMaking = false;
  showDateReporting = false;
  reportedDateType: ReportedDateType;
  minDate: Date;
  maxReportedDate: Date;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: AreaRentalSupervisionApprovalModalData,
    fb: UntypedFormBuilder,
    dialogRef: MatDialogRef<SupervisionApprovalModalComponent>) {
    super(data, fb, dialogRef);
    this.minDate = data.minDate;
    this.maxReportedDate = TimeUtil.minimum(data.lastAreaEndDate, new Date());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.showDateReporting = this.needDateReporting(this.data.taskType, this.resolutionType);

    if (this.showDateReporting) {
      this.initDateReporting();
    }
  }

  toDecisionMaking() {
    const confirmation = this.createConfirmation();
    this.dialogRef.close({
      ...confirmation,
      statusChange: ApplicationStatus.DECISIONMAKING
    });
  }

  protected createConfirmation(): SupervisionApprovalResult {
    const confirmation = super.createConfirmation();
    return {
      ...confirmation,
      statusChange: this.getStatusChange(),
    };
  }

  private initDateReporting(): void {
    const reportedDate = TimeUtil.minimum(this.data.reportedDate, this.maxReportedDate);
    const reportedDateCtrl = this.fb.control(reportedDate, [Validators.required, ComplexValidator.maxDate(this.maxReportedDate)]);
    this.form.addControl('reportedDate', reportedDateCtrl);
    this.reportedDateType = taskTypeToReportedDateType[this.data.taskType];

    reportedDateCtrl.valueChanges.pipe(
      takeUntil(this.destroy),
      startWith(reportedDate),
    ).subscribe(date => this.onReportedDateChange(date));
  }

  private onReportedDateChange(date: Date): void {
    if (this.application.invoicingChanged) {
      this.showToDecisionMaking = true;
    } else {
      this.showToDecisionMaking = this.requiresDecision(date);
    }
  }

  private requiresDecision(date: Date): boolean {
    if (ReportedDateType.WORK_FINISHED === this.reportedDateType) {
      return this.workFinishedRequiresDecision(date);
    } else {
      return false;
    }
  }

  private workFinishedRequiresDecision(date: Date): boolean {
    return !TimeUtil.isSame(date, this.data.comparedDate, 'day');
  }

  private needDateReporting(taskType: SupervisionTaskType, resolutionType: SupervisionApprovalResolutionType): boolean {
    const validTaskType = SupervisionTaskType.FINAL_SUPERVISION === taskType;
    const validResolutionType = resolutionType === SupervisionApprovalResolutionType.APPROVE;
    return validTaskType && validResolutionType;
  }

  private getStatusChange(): ApplicationStatus {
    if (this.showDateReporting) {
      return taskTypeToApplicationStatus[this.data.taskType];
    } else {
      return undefined;
    }
  }
}
