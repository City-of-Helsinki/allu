import {Component, OnDestroy, OnInit} from '@angular/core';

import {MatDialog} from '@angular/material';
import {EnumUtil} from '@util/enum.util';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '@feature/common/ownerModal/owner-modal.component';
import {CurrentUser} from '@service/user/current-user';
import {User} from '@model/user/user';
import {DialogCloseReason} from '@feature/common/dialog-close-value';
import {WorkQueueTab} from './workqueue-tab';
import {NotificationService} from '@feature/notification/notification.service';
import {findTranslation} from '@util/translations';
import {Subject} from 'rxjs';
import {ApplicationWorkItemStore} from './application-work-item-store';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {AddMultiple} from '@feature/project/actions/application-basket-actions';
import {distinctUntilChanged, map, takeUntil} from 'rxjs/internal/operators';
import {UserService} from '@service/user/user-service';
import {ArrayUtil} from '@util/array-util';
import {RoleType} from '@model/user/role-type';

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
  private editRoles = [
      RoleType.ROLE_CREATE_APPLICATION,
      RoleType.ROLE_PROCESS_APPLICATION,
      RoleType.ROLE_DECISION];

  constructor(private itemStore: ApplicationWorkItemStore,
              private dialog: MatDialog,
              private userService: UserService,
              private currentUser: CurrentUser,
              private notification: NotificationService,
              private store: Store<fromRoot.State>) {
  }

  ngOnInit() {
    this.userService.getActiveUsers().subscribe(
        users => this.owners = users.filter(o => ArrayUtil.anyMatch(this.editRoles, o.roles)));

    this.itemStore.changes.pipe(
      map(state => state.selectedItems),
      distinctUntilChanged(),
      takeUntil(this.destroy),
    ).subscribe(items => this.noneSelected = (items.length === 0));
  }

  ngOnDestroy() {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  moveSelectedToSelf() {
    this.currentUser.user.pipe(
      takeUntil(this.destroy)
    ).subscribe(u => this.changeOwner(u));
  }

  addToBasket(): void {
    const selected = this.itemStore.snapshot.selectedItems;
    this.store.dispatch(new AddMultiple(selected));
    this.itemStore.selectedItemsChange([]);
  }

  openHandlerModal() {
    const applicationTypes = this.itemStore.snapshot.page.content
        .filter(a => this.itemStore.snapshot.selectedItems.indexOf(a.id) >= 0)
        .map(a => a.type);
    const config = {
      ...OWNER_MODAL_CONFIG,
      data: {
        type: 'OWNER',
        users: this.owners
            .filter(o => applicationTypes.every(lItem => o.allowedApplicationTypes.indexOf(lItem) >= 0))
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
      () => this.notification.success(findTranslation('workqueue.notifications.ownerChanged')),
      () => this.notification.error(findTranslation('workqueue.notifications.ownerChangeFailed')));
  }

  private removeOwner(): void {
    this.itemStore.removeOwnerFromSelected().subscribe(
      () => this.notification.success(findTranslation('workqueue.notifications.ownerRemoved')),
      () => this.notification.error(findTranslation('workqueue.notifications.ownerRemoveFailed')));
  }
}
