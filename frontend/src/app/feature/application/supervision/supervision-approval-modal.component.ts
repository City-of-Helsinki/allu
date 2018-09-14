import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {ComplexValidator} from '@util/complex-validator';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ApplicationType} from '@model/application/type/application-type';
import {ReportedDateType} from '@feature/application/date-reporting/date-reporting-modal.component';

export const SUPERVISION_APPROVAL_MODAL_CONFIG = {width: '600px', data: {}};

export enum SupervisionApprovalResolutionType {
  APPROVE = 'APPROVE',
  REJECT = 'REJECT',
}

export interface SupervisionApprovalModalData {
  resolutionType: SupervisionApprovalResolutionType;
  taskType: SupervisionTaskType;
  applicationType: ApplicationType;
}

export interface SupervisionApprovalResult {
  result: string;
  newSupervisionDate?: Date;
  reportedDate?: Date;
}

const taskTypeToReportedDateType = {
  OPERATIONAL_CONDITION: ReportedDateType.WINTER_TIME_OPERATION,
  FINAL_SUPERVISION: ReportedDateType.WORK_FINISHED
};

@Component({
  selector: 'supervision-approval-modal',
  templateUrl: './supervision-approval-modal.component.html',
  styleUrls: []
})
export class SupervisionApprovalModalComponent implements OnInit {
  resolutionType: SupervisionApprovalResolutionType;
  form: FormGroup;
  showNewSupervisionDate = false;
  reportedDateType: ReportedDateType;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: SupervisionApprovalModalData,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SupervisionApprovalModalComponent>) {
  }

  ngOnInit(): void {
    this.resolutionType = this.data.resolutionType || SupervisionApprovalResolutionType.APPROVE;
    this.initForm();
  }

  confirm() {
    const formValue = this.form.value;
    this.dialogRef.close({
      result: formValue.result,
      newSupervisionDate: formValue.newSupervisionDate
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
      const newDateDefault = TimeUtil.datePlusWeeks(new Date(), 2);
      this.form.addControl('newSupervisionDate', this.fb.control(newDateDefault, [Validators.required, ComplexValidator.inThePast]));
      this.showNewSupervisionDate = true;
    }

    if (this.showReportedDate(this.data.taskType, this.data.applicationType)) {
      this.form.addControl('reportedDate', this.fb.control(undefined, Validators.required));
      this.reportedDateType = taskTypeToReportedDateType[this.data.taskType];
    }
  }

  private showReportedDate(taskType: SupervisionTaskType, applicationType: ApplicationType): boolean {
    const validApplicationType = ApplicationType.EXCAVATION_ANNOUNCEMENT === applicationType;
    const validTaskType = [SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskType.FINAL_SUPERVISION].indexOf(taskType) >= 0;
    return validApplicationType && validTaskType;
  }
}
