import {Component, OnDestroy, OnInit} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '../common/ownerModal/owner-modal.component';
import {CurrentUser} from '@service/user/current-user';
import {DialogCloseReason} from '@feature/common/dialog-close-value';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {Observable, Subject} from 'rxjs';
import {UserService} from '@service/user/user-service';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {take, takeUntil} from 'rxjs/operators';
import {ChangeOwner, RemoveOwner} from '@feature/application/supervision/actions/supervision-task-actions';

@Component({
  selector: 'supervision-workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: ['./workqueue.component.scss']
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = [WorkQueueTab.OWN, WorkQueueTab.COMMON];
  someSelected$: Observable<boolean>;

  private dialogRef: MatDialogRef<OwnerModalComponent>;
  private activeSupervisors: Array<User> = [];
  private destroy = new Subject<boolean>();

  constructor(
    private currentUser: CurrentUser,
    private userService: UserService,
    private dialog: MatDialog,
    private store: Store<fromRoot.State>) {}

  ngOnInit() {
    this.userService.getByRole(RoleType.ROLE_SUPERVISE)
      .subscribe(supervisors => this.activeSupervisors = supervisors);

    this.someSelected$ = this.store.pipe(select(fromSupervisionWorkQueue.getSomeSelected));
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
    this.store.pipe(
      select(fromSupervisionWorkQueue.getSelected),
      take(1)
    ).subscribe(selected => this.store.dispatch(new ChangeOwner({ownerId: owner.id, taskIds: selected})));
  }

  private removeOwner(): void {
    this.store.pipe(
      select(fromSupervisionWorkQueue.getSelected),
      take(1)
    ).subscribe(selected => this.store.dispatch(new RemoveOwner(selected)));
  }
}
