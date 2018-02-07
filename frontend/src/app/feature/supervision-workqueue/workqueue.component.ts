import {Component, OnDestroy, OnInit} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionWorkItemStore} from './supervision-work-item-store';
import {MatDialog, MatDialogRef} from '@angular/material';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '../common/ownerModal/owner-modal.component';
import {CurrentUser} from '../../service/user/current-user';
import {DialogCloseReason} from '../common/dialog-close-value';
import {User} from '../../model/user/user';
import {NotificationService} from '../../service/notification/notification.service';
import {UserHub} from '../../service/user/user-hub';
import {RoleType} from '../../model/user/role-type';
import {Subscription} from 'rxjs/Subscription';

@Component({
  selector: 'supervision-workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: ['./workqueue.component.scss']
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = [WorkQueueTab[WorkQueueTab.OWN], WorkQueueTab[WorkQueueTab.COMMON]];
  noneSelected = true;

  private dialogRef: MatDialogRef<OwnerModalComponent>;
  private activeSupervisors: Array<User> = [];
  private changeSubscription: Subscription;

  constructor(
    private store: SupervisionWorkItemStore,
    private currentUser: CurrentUser,
    private userHub: UserHub,
    private dialog: MatDialog) {}

  ngOnInit() {
    this.userHub.getByRole(RoleType.ROLE_SUPERVISE)
      .subscribe(supervisors => this.activeSupervisors = supervisors);

    this.changeSubscription = this.store.changes.map(state => state.selectedItems)
      .distinctUntilChanged()
      .subscribe(items => this.noneSelected = (items.length === 0));
  }

  ngOnDestroy() {
    this.changeSubscription.unsubscribe();
  }

  moveSelectedToSelf() {
    this.currentUser.user
      .subscribe(u => this.changeOwner(u));
  }

  openHandlerModal() {
    const config = {
      ...OWNER_MODAL_CONFIG,
      data: {
        type: 'SUPERVISOR',
        users : this.activeSupervisors
      }
    };
    this.dialogRef = this.dialog.open<OwnerModalComponent>(OwnerModalComponent, config);

    this.dialogRef.afterClosed().subscribe(dialogCloseValue => {
      if (dialogCloseValue.reason === DialogCloseReason.OK) {
        if (dialogCloseValue.result) {
          this.changeOwner(dialogCloseValue.result);
        } else {
          this.removeOwner();
        }
      }
      this.dialogRef = undefined;
    });
  }

  private changeOwner(owner: User): void {
    this.store.changeHandlerForSelected(owner.id)
      .subscribe(
        () => NotificationService.translateMessage('supervision.task.action.handlerChanged'),
        () => NotificationService.translateErrorMessage('supervision.task.error.handlerChange')
      );
  }

  private removeOwner(): void {
    this.store.removeHandlerFromSelected().subscribe(
      () => NotificationService.translateMessage('supervision.task.action.handlerRemoved'),
      () => NotificationService.translateErrorMessage('supervision.task.error.handlerRemove')
    );
  }
}
