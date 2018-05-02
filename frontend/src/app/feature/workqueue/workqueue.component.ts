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
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import * as fromProject from '../project/reducers';
import {AddMultiple} from '../project/actions/application-basket-actions';

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

  constructor(private itemStore: ApplicationWorkItemStore,
              private dialog: MatDialog,
              private userHub: UserHub,
              private currentUser: CurrentUser,
              private store: Store<fromRoot.State>) {
  }

  ngOnInit() {
    this.userHub.getActiveUsers().subscribe(users => this.owners = users);

    this.itemStore.changes.map(state => state.selectedItems)
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

  addToBasket(): void {
    const selected = this.itemStore.snapshot.selectedItems;
    this.store.dispatch(new AddMultiple(selected));
    this.itemStore.selectedItemsChange([]);
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
    this.itemStore.changeOwnerForSelected(owner.id).subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerChanged')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerChangeFailed')));
  }

  private removeOwner(): void {
    this.itemStore.removeOwnerFromSelected().subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerRemoved')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerRemoveFailed')));
  }
}
