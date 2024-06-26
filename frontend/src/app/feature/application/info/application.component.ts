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
import * as fromRoot from '@feature/allu/reducers';

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
              private store: Store<fromRoot.State>,
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

    this.sidebarItems = this.createSidebar(application);
  }

  private verifyTypeExists(type: ApplicationType) {
    if (type === undefined) {
      // No known type so navigate back to type selection
      this.router.navigateByUrl('applications/location');
    }
  }

  private createSidebar(application: Application): Array<SidebarItem> {
      return [
        { type: 'BASIC_INFO' },
        ...this.createSidebarItemsForExisting(application),
        ...this.createSidebarItemsForExternal(application)
      ];
  }

  private createSidebarItemsForExisting(application: Application): SidebarItem[] {
    const sidebar: SidebarItem[] = [];
    if (NumberUtil.isExisting(application)) {
      this.sidebarItem(application.type, {type: 'ATTACHMENTS', count: this.attachmentCount }).do(item => sidebar.push(item));
      this.sidebarItem(application.type, {type: 'DECISION'}).do(item => sidebar.push(item));
      this.sidebarItem(application.type, {type: 'SUPERVISION', count: this.taskCount}).do(item => sidebar.push(item));
      this.sidebarItem(application.type, {type: 'INVOICING', warn: this.invoicingWarn}).do(item => sidebar.push(item));
      this.sidebarItem(application.type, {type: 'COMMENTS', count: this.commentCount}).do(item => sidebar.push(item));
      this.sidebarItem(application.type, {type: 'HISTORY'}).do(item => sidebar.push(item));
    }
    return sidebar;
  }

  private createSidebarItemsForExternal(application: Application): SidebarItem[] {
    const sidebar: SidebarItem[] = [];
    if (NumberUtil.isDefined(application.externalOwnerId)) {
      this.sidebarItem(application.type, {type: 'SUPPLEMENTS'}).do(item => sidebar.push(item));
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
