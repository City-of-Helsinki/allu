import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, Subject} from 'rxjs';

import {UrlUtil} from '@util/url.util';
import {ApplicationType} from '@model/application/type/application-type';
import {Application} from '@model/application/application';
import {ApplicationStore} from '@service/application/application-store';
import {SidebarItem, visibleFor} from '@feature/sidebar/sidebar-item';
import {inHandling} from '@model/application/application-status';
import {Option, Some} from '@util/option';
import {NumberUtil} from '@util/number.util';
import * as fromApplication from '../reducers';
import * as fromSupervisionTask from '@feature/application/supervision/reducers';
import {Store} from '@ngrx/store';
import {map, takeUntil} from 'rxjs/internal/operators';

@Component({
  selector: 'application',
  viewProviders: [],
  templateUrl: './application.component.html',
  styleUrls: [
    './application.component.scss'
  ]
})
export class ApplicationComponent implements OnInit, OnDestroy {
  applicationChanges: Observable<Application>;
  readonly: boolean;
  sidebarItems: Array<SidebarItem> = [];

  private destroy = new Subject<boolean>();

  constructor(private route: ActivatedRoute,
              private router: Router,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore) {
  }

  ngOnInit(): void {
    this.applicationChanges = this.applicationStore.application;
    this.applicationChanges.pipe(takeUntil(this.destroy))
      .subscribe(app => this.onApplicationChange(app));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private onApplicationChange(application: Application): void {
    this.verifyTypeExists(ApplicationType[application.type]);

    this.readonly = UrlUtil.urlPathContains(this.route, 'summary');

    const existingApplication = NumberUtil.isDefined(application.id);
    this.sidebarItems = Some(application.type).map(type => this.createSidebar(type, existingApplication)).orElse([]);
  }

  private verifyTypeExists(type: ApplicationType) {
    if (type === undefined) {
      // No known type so navigate back to type selection
      this.router.navigateByUrl('applications/location');
    }
  }

  private createSidebar(applicationType: ApplicationType, existing: boolean): Array<SidebarItem> {
      const sidebar: Array<SidebarItem> = [
        { type: 'BASIC_INFO' }
      ];

      if (existing) {
        this.sidebarItem(applicationType, {type: 'ATTACHMENTS', count: this.attachmentCount }).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'DECISION'}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'SUPERVISION', count: this.taskCount}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'INVOICING', warn: this.invoicingWarn}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'COMMENTS', count: this.commentCount}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'HISTORY'}).do(item => sidebar.push(item));
      }
      return sidebar;
  }

  private get attachmentCount(): Observable<number> {
    return this.applicationStore.attachments.pipe(map(attachments => attachments.length));
  }

  private get commentCount(): Observable<number> {
    return this.store.select(fromApplication.getCommentCount);
  }

  private get taskCount(): Observable<number> {
    return this.store.select(fromSupervisionTask.getSupervisionTaskTotal);
  }

  private get invoicingWarn(): Observable<boolean> {
    const noRecipient = (app: Application) => !NumberUtil.isDefined(app.invoiceRecipientId);
    const isBillable = (app: Application) => !app.notBillable;
    return this.applicationStore.changes.pipe(
      map(change =>
        noRecipient(change.application)
        && isBillable(change.application)
        && inHandling(change.application.status))
    );
  }

  private sidebarItem(appType: ApplicationType, item: SidebarItem): Option<SidebarItem> {
    return Some(visibleFor(ApplicationType[appType], item.type))
      .filter(visible => visible)
      .map(visible => item);
  }
}
