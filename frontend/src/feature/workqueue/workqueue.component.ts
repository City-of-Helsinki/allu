import {Component, OnDestroy, OnInit, ViewContainerRef} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ConnectableObservable, Subscription} from 'rxjs';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {MdTabChangeEvent} from '@angular/material/tabs';
import {MdDialog, MdDialogConfig, MdDialogRef} from '@angular/material';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {translations} from '../../util/translations';
import {EnumUtil} from '../../util/enum.util';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationType} from '../../model/application/type/application-type';
import {Sort} from '../../model/common/sort';
import {HandlerModalComponent} from './handlerModal/handler-modal.component';
import {CurrentUser} from '../../service/user/current-user';
import {User} from '../../model/common/user';
import {UserHub} from '../../service/user/user-hub';
import {DialogCloseReason} from '../common/dialog-close-value';
import {WorkQueueHub} from './workqueue-search/workqueue-hub';
import {WorkQueueTab} from './workqueue-tab';
import {NotificationService} from '../../service/notification/notification.service';

@Component({
  selector: 'workqueue',
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {

  applications: ConnectableObservable<Array<Application>>;
  tabs = EnumUtil.enumValues(WorkQueueTab);
  tab = WorkQueueTab.OWN;
  dialogRef: MdDialogRef<HandlerModalComponent>;
  private selectedApplicationIds = new Array<number>();
  private applicationQuery = new BehaviorSubject<ApplicationSearchQuery>(new ApplicationSearchQuery());
  private sort: Sort;
  private translations = translations;
  private items: Array<string> = ['Ensimmäinen', 'Toinen', 'Kolmas', 'Neljäs', 'Viides'];
  private applicationStatuses = EnumUtil.enumValues(ApplicationStatus);
  private applicationTypes = EnumUtil.enumValues(ApplicationType);
  private handlers: Array<User>;
  private searchQuerySub: Subscription;

  constructor(private applicationHub: ApplicationHub,
              private workqueueHub: WorkQueueHub,
              private dialog: MdDialog,
              private viewContainerRef: ViewContainerRef,
              private userHub: UserHub) { }

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

  tabSelected(event: MdTabChangeEvent) {
    this.tab = WorkQueueTab[this.tabs[event.index]];
  }

  moveSelectedToSelf() {
    console.log('Moving following applications to self', this.selectedApplicationIds);
    let currentUserName = CurrentUser.userName().value();
    this.changeHandler(currentUserName, this.selectedApplicationIds);
  }

  openHandlerModal() {
    let config = new MdDialogConfig();
    config.viewContainerRef = this.viewContainerRef;

    this.dialogRef = this.dialog.open(HandlerModalComponent, config);

    this.dialogRef.afterClosed().subscribe(dialogCloseValue => {
      if (dialogCloseValue.reason === DialogCloseReason.OK) {
        if (dialogCloseValue.result) {
          this.changeHandler(dialogCloseValue.result.userName, this.selectedApplicationIds);
        } else {
          this.removeHandler(this.selectedApplicationIds);
        }
      }
      this.dialogRef = undefined;
    });
  }

  private getApplicationsSearch(query: ApplicationSearchQuery): Observable<Array<Application>> {
    if (this.tab === WorkQueueTab.COMMON) {
      return this.workqueueHub.searchApplicationsSharedByGroup(query);
    } else if (this.tab === WorkQueueTab.OWN) {
      return this.applicationHub.searchApplications(query)
        .map(apps => apps.filter(app => !app.waiting));
    } else {
      return this.applicationHub.searchApplications(query);
    }
  }

  private changeHandler(newHandler: string, ids: Array<number>): void {
    let targetUser = this.handlers.find(handler => handler.userName === newHandler);
    this.applicationHub.changeHandler(targetUser.id, ids).subscribe(
      () => NotificationService.message('Hakemuksien käsittelijä vaihdettu'),
      () => NotificationService.errorMessage('Hakemuksien käsittelijän vaihtaminen epäonnistui'),
      () => this.queryChanged(this.applicationQuery.getValue())); // refresh the view
  }

  private removeHandler(ids: Array<number>): void {
    this.applicationHub.removeHandler(ids).subscribe(
      () => NotificationService.message('Käsittelijä poistettu hakemuksilta'),
      () => NotificationService.errorMessage('Käsittelijän poistaminen hakemuksilta epäonnistui'),
      () => this.queryChanged(this.applicationQuery.getValue())); // refresh the view
  }
}
