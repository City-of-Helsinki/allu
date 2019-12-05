import {BulkApprovalEntry, EntryStatus} from '@app/model/decision/bulk-approval-entry';
import {MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {Component, Inject, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Approve} from '@feature/decision/actions/bulk-approval-actions';
import * as fromDecision from '@feature/decision/reducers';
import {Observable} from 'rxjs';
import {Dictionary} from '@util/object.util';

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
    @Inject(MAT_DIALOG_DATA) public data: BulkApprovalModalData,
    private store: Store<fromDecision.State>
  ) {}

  ngOnInit(): void {
    this.approvableEntries = this.data.entries.filter(entry => !entry.bulkApprovalBlocked);
    this.unapprovableEntries = this.data.entries.filter(entry => entry.bulkApprovalBlocked);
  }

  approve(): void {
    if (this.approvableEntries.length) {
      this.store.dispatch(new Approve(this.approvableEntries));
    }
  }

  cancel(): void {
    this.dialogRef.close();
  }

  status(id: number): Observable<EntryStatus> {
    return this.store.pipe(select(fromDecision.getBulkApprovalEntryStatus(id)));
  }
}
