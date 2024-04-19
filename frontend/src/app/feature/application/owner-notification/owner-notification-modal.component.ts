import {ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {Observable, Subject} from 'rxjs/index';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {ChangeHistoryItem} from '@model/history/change-history-item';
import {LoadByTargetId} from '@feature/history/actions/history-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {RemoveOwnerNotification} from '@feature/application/actions/application-actions';
import {Router} from '@angular/router';

export const OWNER_NOTIFICATION_MODAL_CONFIG = {width: '800px', data: {}};

export interface OwnerNotificationModalData {
  applicationId: number;
}

@Component({
  selector: 'owner-notification-modal',
  templateUrl: './owner-notification-modal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OwnerNotificationModalComponent implements OnInit, OnDestroy {
  changes$: Observable<ChangeHistoryItem[]>;

  private destroy = new Subject<boolean>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: OwnerNotificationModalData,
    private dialogRef: MatDialogRef<OwnerNotificationModalComponent>,
    private store: Store<fromApplication.State>,
    private router: Router) {
  }

  ngOnInit(): void {
    this.changes$ = this.store.pipe(select(fromApplication.getHistory));
    this.store.dispatch(new LoadByTargetId(ActionTargetType.Application, this.data.applicationId));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  close(): void {
    this.dialogRef.close();
  }

  approveAndClose(): void {
    this.store.dispatch(new RemoveOwnerNotification(this.data.applicationId));
    this.dialogRef.close();
  }

  approveAndShowApplication(): void {
    this.store.dispatch(new RemoveOwnerNotification(this.data.applicationId));
    this.router.navigate(['applications', this.data.applicationId, 'summary']);
    this.dialogRef.close();
  }
}
