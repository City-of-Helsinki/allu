import {Component, OnInit} from '@angular/core';
import * as fromDecision from '@feature/decision/reducers';
import {select, Store} from '@ngrx/store';
import {filter, switchMap, take, map} from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import {BULK_APPROVAL_MODAL_CONFIG, BulkApprovalModalComponent} from './bulk-approval-modal.component';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'bulk-approval-modal-container',
  template: ''
})
export class BulkApprovalModalContainerComponent implements OnInit {
  constructor(
    private store: Store<fromDecision.State>,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.store
      .pipe(
        select(fromDecision.getBulkApprovalsLoading),
        filter(loading => !loading),
        switchMap(() => this.store.pipe(select(fromDecision.getBulkApprovalEntries), take(1))),
        map(entries => ({...BULK_APPROVAL_MODAL_CONFIG, data: {entries}})),
        take(1),
        switchMap(config => this.dialog.open(BulkApprovalModalComponent, config).afterClosed()),
      ).subscribe(() => {
        this.router.navigate(['../'], {relativeTo: this.route});
      });
  }
}
