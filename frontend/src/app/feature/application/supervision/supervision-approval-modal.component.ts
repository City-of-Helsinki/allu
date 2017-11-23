import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TimeUtil} from '../../../util/time.util';
import {ComplexValidator} from '../../../util/complex-validator';

export const SUPERVISION_APPROVAL_MODAL_CONFIG = {width: '600px', data: {}};

@Component({
  selector: 'supervision-approval-modal',
  templateUrl: './supervision-approval-modal.component.html',
  styleUrls: []
})
export class SupervisionApprovalModalComponent implements OnInit {
  type: SupervisionApprovalModalType;
  form: FormGroup;
  showNewSupervisionDate = false;

  constructor(
    public fb: FormBuilder,
    public dialogRef: MatDialogRef<SupervisionApprovalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SupervisionApprovalModalData) {
  }

  ngOnInit(): void {
    this.type = this.data.type || 'APPROVE';
    this.form = this.fb.group({
      result: [undefined, Validators.required]
    });

    if ('REJECT' === this.type) {
      const newDateDefault = TimeUtil.datePlusWeeks(new Date(), 2);
      this.form.addControl('newSupervisionDate', this.fb.control(newDateDefault, [Validators.required, ComplexValidator.inThePast]));
      this.showNewSupervisionDate = true;
    }
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
}

export type SupervisionApprovalModalType = 'APPROVE' | 'REJECT';

export interface SupervisionApprovalModalData {
  type: SupervisionApprovalModalType;
}

export interface SupervisionApprovalResult {
  result: string;
  newSupervisionDate?: Date;
}
