import {Component, OnDestroy, OnInit} from '@angular/core';
import 'rxjs/add/operator/publish';
import {MatDialog} from '@angular/material';
import {EnumUtil} from '../../util/enum.util';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '../common/ownerModal/owner-modal.component';
import {CurrentUser} from '../../service/user/current-user';
import {User} from '../../model/user/user';
import {UserHub} from '../../service/user/user-hub';
import {DialogCloseReason} from '../common/dialog-close-value';
import {WorkQueueTab} from './workqueue-tab';
import {NotificationService} from '../../service/notification/notification.service';
import {findTranslation} from '../../util/translations';
import {Subject} from 'rxjs/Subject';
import {ApplicationWorkItemStore} from './application-work-item-store';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: [
    './workqueue.component.scss'
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = EnumUtil.enumValues(WorkQueueTab);
  owners: Array<User>;
  noneSelected = true;

  private destroy = new Subject<boolean>();

  constructor(private store: ApplicationWorkItemStore,
              private dialog: MatDialog,
              private userHub: UserHub,
              private currentUser: CurrentUser) {
  }

  ngOnInit() {
    this.userHub.getActiveUsers().subscribe(users => this.owners = users);

    this.store.changes.map(state => state.selectedItems)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(items => this.noneSelected = (items.length === 0));
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  moveSelectedToSelf() {
    this.currentUser.user
      .takeUntil(this.destroy)
      .subscribe(u => this.changeOwner(u));
  }

  openHandlerModal() {
    const config = {
      ...OWNER_MODAL_CONFIG,
      data: {
        type: 'OWNER',
        users : this.owners
      }
    };

    const dialogRef = this.dialog.open<OwnerModalComponent>(OwnerModalComponent, config);

    dialogRef.afterClosed().subscribe(dialogCloseValue => {
      if (dialogCloseValue.reason === DialogCloseReason.OK) {
        if (dialogCloseValue.result) {
          this.changeOwner(dialogCloseValue.result);
        } else {
          this.removeOwner();
        }
      }
    });
  }

  private changeOwner(owner: User): void {
    this.store.changeOwnerForSelected(owner.id).subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerChanged')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerChangeFailed')));
  }

  private removeOwner(): void {
    this.store.removeOwnerFromSelected().subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerRemoved')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerRemoveFailed')));
  }
}
