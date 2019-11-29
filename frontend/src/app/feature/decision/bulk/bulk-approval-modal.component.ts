import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {Component, Inject, OnInit} from '@angular/core';

export interface BulkApprovalModalData {
  entries: BulkApprovalEntry[];
}

export const BULK_APPROVAL_MODAL_CONFIG: MatDialogConfig<BulkApprovalModalData> = {
  width: '600px',
  data: undefined
};

@Component({
  selector: 'bulk-approval-modal',
  templateUrl: './bulk-approval-modal.component.html',
  styleUrls: ['./bulk-approval-modal.component.scss']
})
export class BulkApprovalModalComponent implements OnInit {
  approvableEntries: BulkApprovalEntry[] = [];
  unapprovableEntries: BulkApprovalEntry[] = [];

  constructor(
    public dialogRef: MatDialogRef<BulkApprovalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: BulkApprovalModalData
  ) {}

  ngOnInit(): void {
    this.approvableEntries = this.data.entries.filter(entry => !entry.bulkApprovalBlocked);
    this.unapprovableEntries = this.data.entries.filter(entry => entry.bulkApprovalBlocked);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
