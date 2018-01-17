import {Component, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/publish';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {MatDialog, MatDialogRef, MatTabChangeEvent} from '@angular/material';

import {Application} from '../../model/application/application';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {EnumUtil} from '../../util/enum.util';
import {Sort} from '../../model/common/sort';
import {HANDLER_MODAL_CONFIG, HandlerModalComponent} from '../common/handlerModal/handler-modal.component';
import {CurrentUser} from '../../service/user/current-user';
import {User} from '../../model/user/user';
import {UserHub} from '../../service/user/user-hub';
import {DialogCloseReason} from '../common/dialog-close-value';
import {WorkQueueHub} from './workqueue-search/workqueue-hub';
import {WorkQueueTab} from './workqueue-tab';
import {NotificationService} from '../../service/notification/notification.service';
import {ConnectableObservable} from 'rxjs/observable/ConnectableObservable';
import {Subscription} from 'rxjs/Subscription';
import {ApplicationService} from '../../service/application/application.service';
import {findTranslation} from '../../util/translations';

@Component({
  selector: 'workqueue',
  templateUrl: './workqueue.component.html',
  styleUrls: [
    './workqueue.component.scss'
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {

  applications: ConnectableObservable<Array<Application>>;
  tabs = EnumUtil.enumValues(WorkQueueTab);
  tab = WorkQueueTab.OWN;
  dialogRef: MatDialogRef<HandlerModalComponent>;
  handlers: Array<User>;
  private selectedApplicationIds = new Array<number>();
  private applicationQuery = new BehaviorSubject<ApplicationSearchQuery>(new ApplicationSearchQuery());
  private sort: Sort;
  private searchQuerySub: Subscription;

  constructor(private applicationService: ApplicationService,
              private workqueueHub: WorkQueueHub,
              private dialog: MatDialog,
              private userHub: UserHub,
              private currentUser: CurrentUser) { }

  ngOnInit() {
    this.applications = this.applicationQuery.asObservable()
      .debounceTime(500)
      .distinctUntilChanged()
      .switchMap(query => this.getApplicationsSearch(query))
      .catch(err => NotificationService.errorCatch(err, []))
      .publish();

    this.userHub.getActiveUsers().subscribe(users => this.handlers = users);
    this.searchQuerySub = this.workqueueHub.searchQuery.subscribe(query => this.queryChanged(query));
  }

  ngOnDestroy() {
    this.searchQuerySub.unsubscribe();
  }

  queryChanged(query: ApplicationSearchQuery) {
    this.applicationQuery.next(query.withSort(this.sort));
  }

  sortChanged(sort: Sort) {
    // use old query parameters and new sort
    this.sort = sort;
    this.queryChanged(this.applicationQuery.getValue());
  }

  selectionChanged(applicationIds: Array<number>) {
    this.selectedApplicationIds = applicationIds;
  }

  tabSelected(event: MatTabChangeEvent) {
    this.tab = WorkQueueTab[this.tabs[event.index]];
  }

  moveSelectedToSelf() {
    this.currentUser.user
      .subscribe(u => this.changeOwner(u, this.selectedApplicationIds));
  }

  openHandlerModal() {
    const config = {
      ...HANDLER_MODAL_CONFIG,
      data: {
        type: 'HANDLER',
        users : this.handlers
      }
    };

    this.dialogRef = this.dialog.open<HandlerModalComponent>(HandlerModalComponent, config);

    this.dialogRef.afterClosed().subscribe(dialogCloseValue => {
      if (dialogCloseValue.reason === DialogCloseReason.OK) {
        if (dialogCloseValue.result) {
          this.changeOwner(dialogCloseValue.result, this.selectedApplicationIds);
        } else {
          this.removeOwner(this.selectedApplicationIds);
        }
      }
      this.dialogRef = undefined;
    });
  }

  private getApplicationsSearch(query: ApplicationSearchQuery): Observable<Array<Application>> {
    if (this.tab === WorkQueueTab.COMMON) {
      return this.workqueueHub.searchApplicationsSharedByGroup(query);
    } else if (this.tab === WorkQueueTab.OWN) {
      return this.applicationService.search(query)
        .map(apps => apps.filter(app => !app.waiting));
    } else {
      return this.applicationService.search(query);
    }
  }

  private changeOwner(owner: User, ids: Array<number>): void {
    this.applicationService.changeOwner(owner.id, ids).subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerChanged')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerChangeFailed')),
      () => this.queryChanged(this.applicationQuery.getValue())); // refresh the view
  }

  private removeOwner(ids: Array<number>): void {
    this.applicationService.removeOwner(ids).subscribe(
      () => NotificationService.message(findTranslation('workqueue.notifications.ownerRemoved')),
      () => NotificationService.errorMessage(findTranslation('workqueue.notifications.ownerRemoveFailed')),
      () => this.queryChanged(this.applicationQuery.getValue())); // refresh the view
  }
}
