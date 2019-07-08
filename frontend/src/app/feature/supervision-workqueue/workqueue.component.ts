import {Component, OnDestroy, OnInit} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionWorkItemStore} from './supervision-work-item-store';
import {MatDialog, MatDialogRef} from '@angular/material';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '../common/ownerModal/owner-modal.component';
import {CurrentUser} from '@service/user/current-user';
import {DialogCloseReason} from '@feature/common/dialog-close-value';
import {User} from '@model/user/user';
import {NotificationService} from '@feature/notification/notification.service';
import {RoleType} from '@model/user/role-type';
import {Subscription} from 'rxjs';
import {distinctUntilChanged, map} from 'rxjs/internal/operators';
import {UserService} from '@service/user/user-service';

@Component({
  selector: 'supervision-workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: ['./workqueue.component.scss']
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = [WorkQueueTab.OWN, WorkQueueTab.COMMON];
  noneSelected = true;

  private dialogRef: MatDialogRef<OwnerModalComponent>;
  private activeSupervisors: Array<User> = [];
  private changeSubscription: Subscription;

  constructor(
    private store: SupervisionWorkItemStore,
    private currentUser: CurrentUser,
    private userService: UserService,
    private dialog: MatDialog,
    private notification: NotificationService) {}

  ngOnInit() {
    this.userService.getByRole(RoleType.ROLE_SUPERVISE)
      .subscribe(supervisors => this.activeSupervisors = supervisors);

    this.changeSubscription = this.store.changes.pipe(
      map(state => state.selectedItems),
      distinctUntilChanged()
    ).subscribe(items => this.noneSelected = (items.length === 0));
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
        () => this.notification.translateSuccess('supervision.task.action.handlerChanged'),
        () => this.notification.translateErrorMessage('supervision.task.error.handlerChange')
      );
  }

  private removeOwner(): void {
    this.store.removeHandlerFromSelected().subscribe(
      () => this.notification.translateSuccess('supervision.task.action.handlerRemoved'),
      () => this.notification.translateErrorMessage('supervision.task.error.handlerRemove')
    );
  }
}
