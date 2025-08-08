import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromApplication from '@feature/application/reducers';
import { MatDialog } from '@angular/material/dialog';
import {
  OWNER_NOTIFICATION_MODAL_CONFIG,
  OwnerNotificationModalComponent
} from '@feature/application/owner-notification/owner-notification-modal.component';
import {RemoveOwnerNotification} from '@feature/application/actions/application-actions';

@Component({
  selector: 'owner-notification',
  templateUrl: './owner-notification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OwnerNotificationComponent {

  @Input() applicationId: number;

  constructor(
    private store: Store<fromApplication.State>,
    private dialog: MatDialog
  ) {}

  show(): void {
    const config = { ...OWNER_NOTIFICATION_MODAL_CONFIG, data: { applicationId: this.applicationId } };
    this.dialog.open(OwnerNotificationModalComponent, config);
  }

  approve(): void {
    this.store.dispatch(new RemoveOwnerNotification(this.applicationId));
  }
}
