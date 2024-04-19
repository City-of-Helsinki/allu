import {Component, OnDestroy, OnInit} from '@angular/core';

import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {OWNER_MODAL_CONFIG, OwnerModalComponent} from '@feature/common/ownerModal/owner-modal.component';
import {CurrentUser} from '@service/user/current-user';
import {User} from '@model/user/user';
import {DialogCloseReason, DialogCloseValue} from '@feature/common/dialog-close-value';
import {workQueueTabs} from './workqueue-tab';
import {Observable, Subject} from 'rxjs';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import {AddMultiple} from '@feature/project/actions/application-basket-actions';
import {map, switchMap, take, takeUntil, withLatestFrom} from 'rxjs/operators';
import {UserService} from '@service/user/user-service';
import {ArrayUtil} from '@util/array-util';
import {RoleType} from '@model/user/role-type';
import {ClearSelected} from '@feature/application/actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ChangeOwner, RemoveOwner} from '@feature/application/actions/application-actions';
import {ApplicationType} from '@model/application/type/application-type';
import {Load as LoadBulkApprovalEntries} from '@feature/decision/actions/bulk-approval-actions';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationStatus} from '@model/application/application-status';

@Component({
  selector: 'workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: [
    './workqueue.component.scss'
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = workQueueTabs;
  owners: Array<User>;
  someSelected$: Observable<boolean>;
  someDecisionMakingSelected$: Observable<boolean>;

  private destroy = new Subject<boolean>();
  private editRoles = [
      RoleType.ROLE_CREATE_APPLICATION,
      RoleType.ROLE_PROCESS_APPLICATION,
      RoleType.ROLE_DECISION];

  constructor(
    private dialog: MatDialog,
    private userService: UserService,
    private currentUser: CurrentUser,
    private store: Store<fromRoot.State>,
    private router: Router,
    private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.userService.getActiveUsers().subscribe(
        users => this.owners = users.filter(o => ArrayUtil.anyMatch(this.editRoles, o.roles)));

    this.someSelected$ = this.store.pipe(select(fromWorkQueue.getSomeApplicationsSelected));
    this.someDecisionMakingSelected$ = this.store.pipe(select(fromWorkQueue.getSomeSelectedHaveStatus(ApplicationStatus.DECISIONMAKING)));
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
    this.store.pipe(
      select(fromWorkQueue.getSelectedApplications),
      take(1)
    ).subscribe(selected => {
      this.store.dispatch(new AddMultiple(selected));
      this.store.dispatch(new ClearSelected(ActionTargetType.Application));
    });
  }

  openHandlerModal() {
    this.store.pipe(
      select(fromWorkQueue.getSelectedApplicationEntities),
      take(1),
      map(selected => selected.map(app => app.type)),
      map(selectedTypes => this.getOwnersWhoCanOwn(selectedTypes)),
      map(owners => this.createConfig(owners)),
      switchMap(config => this.dialog.open<OwnerModalComponent>(OwnerModalComponent, config).afterClosed())
    ).subscribe(dialogCloseValue => this.handleOwnerChange(dialogCloseValue));
  }

  bulkApprove(): void {
    this.store.pipe(
      select(fromWorkQueue.getSelectedApplications),
      withLatestFrom(this.store.pipe(select(fromWorkQueue.getTab))),
      take(1)
    ).subscribe(([selected, tab]) => {
      this.store.dispatch(new LoadBulkApprovalEntries(selected));
      this.store.dispatch(new ClearSelected(ActionTargetType.Application));
      this.router.navigate([tab.toLocaleLowerCase(), 'bulkApproval'], {relativeTo: this.route});
    });
  }

  /**
   * Returns users who can own every selected type
   */
  private getOwnersWhoCanOwn(selectedTypes: ApplicationType[]): User[] {
    return this.owners.filter(owner =>
      selectedTypes.every(type => owner.allowedApplicationTypes.indexOf(type) >= 0));
  }

  private createConfig(owners: User[]) {
    return {
      ...OWNER_MODAL_CONFIG,
      data: {
        type: 'OWNER',
        users: owners
      }
    };
  }

  private handleOwnerChange(dialogCloseValue: DialogCloseValue): void {
    if (dialogCloseValue.reason === DialogCloseReason.OK) {
      if (dialogCloseValue.result) {
        this.changeOwner(dialogCloseValue.result);
      } else {
        this.removeOwner();
      }
    }
  }

  private changeOwner(owner: User): void {
    this.store.pipe(
      select(fromWorkQueue.getSelectedApplications),
      take(1)
    ).subscribe(selected => this.store.dispatch(new ChangeOwner(owner.id, selected)));
  }

  private removeOwner(): void {
    this.store.pipe(
      select(fromWorkQueue.getSelectedApplications),
      take(1)
    ).subscribe(selected => this.store.dispatch(new RemoveOwner(selected)));
  }
}
