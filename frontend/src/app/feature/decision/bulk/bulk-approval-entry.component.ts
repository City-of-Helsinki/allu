import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {BulkApprovalEntry, EntryStatus} from '@model/decision/bulk-approval-entry';

@Component({
  selector: 'bulk-approval-entry',
  templateUrl: './bulk-approval-entry.component.html',
  styleUrls: ['./bulk-approval-entry.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BulkApprovalEntryComponent {
  @Input() entry: BulkApprovalEntry;
  @Input() entryStatus: EntryStatus;
}
