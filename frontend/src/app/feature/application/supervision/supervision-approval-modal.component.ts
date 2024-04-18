import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {ComplexValidator} from '@util/complex-validator';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {Subject} from 'rxjs/index';
import {Application} from '@model/application/application';
import {ApplicationStatus} from '@model/application/application-status';
import {RequiredTasks} from '@model/application/required-tasks';

export const SUPERVISION_APPROVAL_MODAL_CONFIG = {width: '600px', data: {}};

export enum SupervisionApprovalResolutionType {
  APPROVE = 'APPROVE',
  REJECT = 'REJECT',
}

export interface SupervisionApprovalModalData {
  resolutionType: SupervisionApprovalResolutionType;
  taskType: SupervisionTaskType;
  application: Application;
}

export interface SupervisionApprovalResult {
  result: string;
  newSupervisionDate?: Date;
  reportedDate?: Date;
  statusChange?: ApplicationStatus;
  requiredTasks?: RequiredTasks;
}

@Component({
  selector: 'supervision-approval-modal',
  templateUrl: './supervision-approval-modal.component.html',
  styleUrls: []
})
export class SupervisionApprovalModalComponent implements OnInit, OnDestroy {
  resolutionType: SupervisionApprovalResolutionType;
  form: UntypedFormGroup;
  showNewSupervisionDate = false;
  application: Application;

  protected destroy = new Subject<boolean>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: SupervisionApprovalModalData,
    protected fb: UntypedFormBuilder,
    protected dialogRef: MatDialogRef<SupervisionApprovalModalComponent>) {
  }

  ngOnInit(): void {
    this.application = this.data.application;
    this.resolutionType = this.data.resolutionType || SupervisionApprovalResolutionType.APPROVE;
    this.initForm();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  confirm() {
    this.dialogRef.close(this.createConfirmation());
  }

  cancel() {
    this.dialogRef.close();
  }

  protected initForm(): void {
    this.form = this.fb.group({
      result: [undefined, Validators.required]
    });

    if (SupervisionApprovalResolutionType.REJECT === this.resolutionType) {
      this.initReject();
    }
  }

  private initReject(): void {
    const newDateDefault = TimeUtil.datePlusWeeks(new Date(), 2);
    this.form.addControl('newSupervisionDate', this.fb.control(newDateDefault, [Validators.required, ComplexValidator.inThePast]));
    this.showNewSupervisionDate = true;
  }

  protected createConfirmation(): SupervisionApprovalResult {
    const formValue = this.form.value;
    return {
      result: formValue.result,
      newSupervisionDate: formValue.newSupervisionDate,
      reportedDate: formValue.reportedDate
    };
  }
}
