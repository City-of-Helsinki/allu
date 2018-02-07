import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {UrlUtil} from '../../../util/url.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {SidebarItem, visibleFor} from '../../sidebar/sidebar-item';
import {ProgressStep, stepFrom} from '../progressbar/progress-step';
import {ApplicationStatus, inHandling} from '../../../model/application/application-status';
import {AttachmentHub} from '../attachment/attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Option, Some} from '../../../util/option';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {NumberUtil} from '../../../util/number.util';
import {Subject} from 'rxjs/Subject';
import {DefaultRecipientHub} from '../../../service/recipients/default-recipient-hub';
import {DefaultRecipient} from '../../../model/common/default-recipient';
import {DistributionEntry} from '../../../model/common/distribution-entry';
import {DistributionType} from '../../../model/common/distribution-type';
import {FixedLocationService} from '../../../service/map/fixed-location.service';

@Component({
  selector: 'application',
  viewProviders: [],
  templateUrl: './application.component.html',
  styleUrls: [
    './application.component.scss'
  ]
})
export class ApplicationComponent implements OnInit, OnDestroy {
  progressStep: ProgressStep;
  applicationChanges: Observable<Application>;
  readonly: boolean;
  sidebarItems: Array<SidebarItem> = [];

  private destroy = new Subject<boolean>();

  constructor(private route: ActivatedRoute,
              private router: Router,
              private applicationStore: ApplicationStore,
              private attachmentHub: AttachmentHub,
              private fixedLocationService: FixedLocationService,
              private supervisionTaskStore: SupervisionTaskStore,
              private defaultRecipientHub: DefaultRecipientHub) {
  }

  ngOnInit(): void {
    const application = this.applicationStore.snapshot.application;
    this.initDefaultAttachments(application);
    this.initDistribution(application);

    this.applicationChanges = this.applicationStore.application;
    this.applicationChanges
      .takeUntil(this.destroy)
      .subscribe(app => this.onApplicationChange(app));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private onApplicationChange(application: Application): void {
    Some(application.id).do(id => this.supervisionTaskStore.loadTasks(id));
    this.verifyTypeExists(ApplicationType[application.type]);

    this.readonly = UrlUtil.urlPathContains(this.route, 'summary');
    this.progressStep = stepFrom(ApplicationStatus[application.status], this.readonly);

    const existingApplication = NumberUtil.isDefined(application.id);
    this.sidebarItems = Some(application.typeEnum).map(type => this.createSidebar(type, existingApplication)).orElse([]);
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
        this.sidebarItem(applicationType, {type: 'COMMENTS', count: this.commentCount}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'HISTORY'}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'DECISION'}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'SUPERVISION', count: this.taskCount}).do(item => sidebar.push(item));
        this.sidebarItem(applicationType, {type: 'INVOICING', warn: this.invoicingWarn}).do(item => sidebar.push(item));
      }
      return sidebar;
  }

  private get attachmentCount(): Observable<number> {
    return this.applicationStore.attachments.map(attachments => attachments.length);
  }

  private get commentCount(): Observable<number> {
    return this.applicationStore.comments.map(comments => comments.length);
  }

  private get taskCount(): Observable<number> {
    return this.supervisionTaskStore.tasks.map(supervisions => supervisions.length);
  }

  private get invoicingWarn(): Observable<boolean> {
    const noRecipient = (app: Application) => !NumberUtil.isDefined(app.invoiceRecipientId);
    const isBillable = (app: Application) => !app.notBillable;
    return this.applicationStore.changes.map(change =>
      noRecipient(change.application)
      && isBillable(change.application)
      && inHandling(change.application.statusEnum));
  }

  private sidebarItem(appType: ApplicationType, item: SidebarItem): Option<SidebarItem> {
    return Some(visibleFor(ApplicationType[appType], item.type))
      .filter(visible => visible)
      .map(visible => item);
  }

  private initDefaultAttachments(application: Application): void {
    this.defaultAttachmentsForArea(application)
      .takeWhile(() => this.applicationStore.isNew) // Only add default attachments if it is a new application
      .takeUntil(this.destroy)
      .subscribe(
        attachments => attachments.forEach(a => this.applicationStore.saveAttachment(a)),
        err => NotificationService.errorMessage(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private initDistribution(application: Application): void {
    this.defaultRecipientHub.defaultRecipientsByApplicationType(application.type)
      .takeWhile(() => this.applicationStore.isNew) // Only add default attachments if it is a new application
      .takeUntil(this.destroy)
      .map(recipients => recipients.map(r => this.toDistributionEntry(r)))
      .subscribe(distributionEntries => {
        application.decisionDistributionList = distributionEntries;
        this.applicationStore.applicationChange(application);
      }, err => NotificationService.errorMessage(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private defaultAttachmentsForArea(application: Application): Observable<Array<DefaultAttachmentInfo>> {
    return Some(application.firstLocation)
      .map(loc => loc.fixedLocationIds)
      .map(ids => this.fixedLocationService.areaBySectionIds(ids)
        .switchMap(area => this.attachmentHub.defaultAttachmentInfosByArea(application.typeEnum, area.id)))
      .orElse(Observable.of([]));
  }

  private toDistributionEntry(recipient: DefaultRecipient): DistributionEntry {
    const de = new DistributionEntry();
    de.name = recipient.email;
    de.email = recipient.email;
    de.distributionType = DistributionType.EMAIL;
    return de;
  }
}
