import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable, of, Subject} from 'rxjs';

import {UrlUtil} from '../../../util/url.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {SidebarItem, visibleFor} from '../../sidebar/sidebar-item';
import {inHandling} from '../../../model/application/application-status';
import {AttachmentHub} from '../attachment/attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {NotificationService} from '../../notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Option, Some} from '../../../util/option';
import {NumberUtil} from '../../../util/number.util';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {DefaultRecipient} from '../../../model/common/default-recipient';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {DistributionType} from '../../../model/common/distribution-type';
import {FixedLocationService} from '../../../service/map/fixed-location.service';
import * as fromApplication from '../reducers';
import * as fromSupervisionTask from '@feature/application/supervision/reducers';
import {Store} from '@ngrx/store';
import {map, switchMap, takeUntil, takeWhile} from 'rxjs/internal/operators';
import {CurrentUser} from '@service/user/current-user';

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
              private applicationStore: ApplicationStore,
              private attachmentHub: AttachmentHub,
              private fixedLocationService: FixedLocationService,
              private defaultRecipientHub: DefaultRecipientHub,
              private notification: NotificationService,
              private currentUser: CurrentUser) {
  }

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.initDefaultAttachments(application);
    this.initDistribution(application);
    this.addCurrentUserToDistribution(application);

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

  private initDefaultAttachments(application: Application): void {
    this.defaultAttachmentsForArea(application).pipe(
      takeWhile(() => this.applicationStore.isNew), // Only add default attachments if it is a new application
      takeUntil(this.destroy)
    ).subscribe(
      attachments => attachments.forEach(a => this.applicationStore.saveAttachment(a)),
      err => this.notification.error(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private initDistribution(application: Application): void {
    this.defaultRecipientHub.defaultRecipientsByApplicationType(application.type).pipe(
      takeWhile(() => this.applicationStore.isNew), // Only add default attachments if it is a new application
      takeUntil(this.destroy),
      map(recipients => recipients.map(r => this.toDistributionEntry(r)))
    ).subscribe(distributionEntries => {
      application.decisionDistributionList.push(...distributionEntries);
      this.applicationStore.applicationChange(application);
    }, err => this.notification.error(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private addCurrentUserToDistribution(application: Application): void {
    const existingApplication = NumberUtil.isDefined(application.id);
    if (!existingApplication && (application.type === ApplicationType.EVENT
                              || application.type === ApplicationType.SHORT_TERM_RENTAL)) {
      this.currentUser.user.subscribe(user => {
        const entry = new DistributionEntry(null, user.realName, DistributionType.EMAIL, user.emailAddress);
        application.decisionDistributionList.push(entry);
        this.applicationStore.applicationChange(application);
      });
    }
  }

  private defaultAttachmentsForArea(application: Application): Observable<Array<DefaultAttachmentInfo>> {
    return Some(application.firstLocation)
      .map(loc => loc.fixedLocationIds)
      .map(ids => this.fixedLocationService.areaBySectionIds(ids).pipe(
        switchMap(area => this.attachmentHub.defaultAttachmentInfosByArea(application.type, area.id))
      )).orElse(of([]));
  }

  private toDistributionEntry(recipient: DefaultRecipient): DistributionEntry {
    const de = new DistributionEntry();
    de.name = recipient.email;
    de.email = recipient.email;
    de.distributionType = DistributionType.EMAIL;
    return de;
  }
}
