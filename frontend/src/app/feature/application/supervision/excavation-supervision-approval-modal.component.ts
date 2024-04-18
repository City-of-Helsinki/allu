import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ReportedDateType} from '@feature/application/date-reporting/date-reporting-modal.component';
import {map, startWith, take, takeUntil, withLatestFrom} from 'rxjs/internal/operators';
import {Observable, of} from 'rxjs/index';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import * as fromRoot from '@feature/allu/reducers';
import * as fromSupervision from '@feature/application/supervision/reducers';
import {Store} from '@ngrx/store';
import {ApplicationStatus} from '@model/application/application-status';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {
  SupervisionApprovalModalComponent,
  SupervisionApprovalModalData,
  SupervisionApprovalResolutionType,
  SupervisionApprovalResult
} from '@feature/application/supervision/supervision-approval-modal.component';
import {RequiredTasks} from '@model/application/required-tasks';
import {ComplexValidator} from '@util/complex-validator';

export interface ExcavationSupervisionApprovalModalData extends SupervisionApprovalModalData {
  reportedDate?: Date;
  comparedDate?: Date;
  minDate?: Date;
  compactionAndBearingCapacityMeasurement?: boolean;
  qualityAssuranceTest?: boolean;
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
  selector: 'excavation-supervision-approval-modal',
  templateUrl: './excavation-supervision-approval-modal.component.html',
  styleUrls: []
})
export class ExcavationSupervisionApprovalModalComponent extends SupervisionApprovalModalComponent implements OnInit {
  showToDecisionMaking = false;
  showDateReporting = false;
  showRequiredTasks = false;
  reportedDateType: ReportedDateType;
  maxReportedDate: Date;
  minReportedDate: Date;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ExcavationSupervisionApprovalModalData,
    fb: UntypedFormBuilder,
    dialogRef: MatDialogRef<SupervisionApprovalModalComponent>,
    private store: Store<fromRoot.State>,
    private configurationHelper: ConfigurationHelperService) {
    super(data, fb, dialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.showDateReporting = this.needDateReporting(this.data.taskType, this.resolutionType);
    this.showRequiredTasks = SupervisionTaskType.PRELIMINARY_SUPERVISION === this.data.taskType;
    this.maxReportedDate = new Date();
    this.minReportedDate = this.data.minDate;
    if (this.showDateReporting) {
      this.initDateReporting();
    }

    if (this.showRequiredTasks) {
      this.initRequiredTasks();
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
      requiredTasks: this.getRequiredTasks()
    };
  }

  private initDateReporting(): void {
    const reportedDate = TimeUtil.minimum(this.data.reportedDate, this.maxReportedDate);
    const reportedDateCtrl = this.fb.control(reportedDate, [
      Validators.required,
      ComplexValidator.minDate(this.minReportedDate),
      ComplexValidator.maxDate(this.maxReportedDate)
    ]);
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
      withLatestFrom(this.hasOpenOperationalConditionTask()),
      take(1),
      map(([finishesInWinter, openTask]) => openTask || (dateChange && !(isWinterTimeOperation && finishesInWinter)))
    );
  }

  private needDateReporting(taskType: SupervisionTaskType, resolutionType: SupervisionApprovalResolutionType): boolean {
    const validTaskType = [SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskType.FINAL_SUPERVISION].indexOf(taskType) >= 0;
    const validResolutionType = resolutionType === SupervisionApprovalResolutionType.APPROVE;
    return validTaskType && validResolutionType;
  }

  private initRequiredTasks(): void {
    const measurementCtrl = this.fb.control(this.data.compactionAndBearingCapacityMeasurement);
    const qualityAssuranceTest = this.fb.control(this.data.qualityAssuranceTest);
    this.form.addControl('compactionAndBearingCapacityMeasurement', measurementCtrl);
    this.form.addControl('qualityAssuranceTest', qualityAssuranceTest);
  }

  private getStatusChange(): ApplicationStatus {
    if (this.showDateReporting) {
      return taskTypeToApplicationStatus[this.data.taskType];
    } else {
      return undefined;
    }
  }

  private getRequiredTasks(): RequiredTasks {
    const formValue = this.form.value;
    if (formValue.qualityAssuranceTest || formValue.compactionAndBearingCapacityMeasurement) {
      return {
        qualityAssuranceTest: formValue.qualityAssuranceTest,
        compactionAndBearingCapacityMeasurement: formValue.compactionAndBearingCapacityMeasurement
      };
    } else {
      return undefined;
    }
  }

  private hasOpenOperationalConditionTask(): Observable<boolean> {
    return this.store.select(fromSupervision.getOpenOperationalConditionTask).pipe(
      map(task => !!task)
    );
  }
}
