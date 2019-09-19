import {ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Observable, Subject} from 'rxjs/index';
import {select, Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import {ChangeHistoryItem} from '@model/history/change-history-item';
import {LoadByTargetId} from '@feature/history/actions/history-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

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
    private store: Store<fromApplication.State>) {
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
}
