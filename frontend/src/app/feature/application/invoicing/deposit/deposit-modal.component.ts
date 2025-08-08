import {Component, Inject, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Deposit} from '@model/application/invoice/deposit';
import {EnumUtil} from '@util/enum.util';
import {DepositStatusType} from '@model/application/invoice/deposit-status-type';

export const DEPOSIT_MODAL_CONFIG = {
  width: '800PX',
  disableClose: false,
  data: {
    deposit: new Deposit()
  }
};

export interface DepositModalData {
  deposit: Deposit;
}

@Component({
  selector: 'deposit-modal',
  templateUrl: './deposit-modal.component.html',
  styleUrls: []
})
export class DepositModalComponent implements OnInit {
  depositForm: UntypedFormGroup;
  depositStatuses = EnumUtil.enumValues(DepositStatusType);

  constructor(public dialogRef: MatDialogRef<DepositModalComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DepositModalData,
              private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    const deposit = this.data.deposit;

    this.depositForm = this.fb.group({
      id: [deposit.id],
      applicationId: [deposit.applicationId],
      amount: [deposit.amountEuro],
      reason: [deposit.reason],
      status: [deposit.uiStatus]
    });
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }

  onSubmit(): void {
    const deposit = Deposit.fromForm(this.depositForm.getRawValue());
    this.dialogRef.close(deposit);
  }
}
