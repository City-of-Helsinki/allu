import {Component, Input} from '@angular/core';
import {BulkApprovalEntry, EntryStatus} from '@model/decision/bulk-approval-entry';
import {select, Store} from '@ngrx/store';
import * as fromDecision from '@feature/decision/reducers';
import {Observable} from 'rxjs';

@Component({
  selector: 'bulk-approval-entry-list',
  templateUrl: './bulk-approval-entry-list.component.html',
  styleUrls: ['./bulk-approval-entry-list.component.scss']
})
export class BulkApprovalEntryListComponent {
  @Input() entries: BulkApprovalEntry[];

  constructor(private store: Store<fromDecision.State>) {
  }

  status(id: number): Observable<EntryStatus> {
    return this.store.pipe(select(fromDecision.getBulkApprovalEntryStatus(id)));
  }
}
