import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {UrlUtil} from '../../../util/url.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {SidebarItem, visibleFor} from '../../sidebar/sidebar-item';
import {ProgressStep, stepFrom} from '../progressbar/progress-step';
import {ApplicationStatus, inHandling} from '../../../model/application/application-status';
import {AttachmentHub} from '../attachment/attachment-hub';
import {MapHub} from '../../../service/map/map-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Option, Some} from '../../../util/option';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {NumberUtil} from '../../../util/number.util';

@Component({
  selector: 'application',
  viewProviders: [],
  templateUrl: './application.component.html',
  styleUrls: [
    './application.component.scss'
  ]
})
export class ApplicationComponent implements OnInit {
  progressStep: ProgressStep;
  application: Application;
  readonly: boolean;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private applicationStore: ApplicationStore,
              private attachmentHub: AttachmentHub,
              private mapHub: MapHub,
              private supervisionTaskStore: SupervisionTaskStore) {
  }

  ngOnInit(): void {
    this.application = this.applicationStore.snapshot.application;
    Some(this.application.id).do(id => this.supervisionTaskStore.loadTasks(id));
    this.verifyTypeExists(ApplicationType[this.application.type]);

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.readonly = summary;
      this.progressStep = stepFrom(ApplicationStatus[this.application.status], summary);

      this.defaultAttachmentsForArea(this.application.typeEnum).subscribe(
        attachments => attachments.forEach(a => this.applicationStore.addAttachment(a)),
        err => NotificationService.errorMessage(findTranslation('attachment.error.defaultAttachmentByArea')));

      this.sidebarItems = this.createSidebar(summary);
    });
  }

  verifyTypeExists(type: ApplicationType) {
    if (type === undefined) {
      // No known type so navigate back to type selection
      this.router.navigateByUrl('applications/location');
    }
  }

  private createSidebar(summary: boolean): Array<SidebarItem> {
      const sidebar: Array<SidebarItem> = [
        { type: 'BASIC_INFO' },
        { type: 'ATTACHMENTS', count: this.attachmentCount }
      ];

      if (summary) {
        this.sidebarItem({type: 'COMMENTS', count: this.commentCount}).do(item => sidebar.push(item));
        this.sidebarItem({type: 'HISTORY'}).do(item => sidebar.push(item));
        this.sidebarItem({type: 'DECISION'}).do(item => sidebar.push(item));
        this.sidebarItem({type: 'SUPERVISION', count: this.taskCount}).do(item => sidebar.push(item));
        this.sidebarItem({type: 'INVOICING', warn: this.invoicingWarn})
          .do(item => sidebar.push(item));
      }
      return sidebar;
  }

  private get attachmentCount(): Observable<number> {
    return Observable.combineLatest(
      this.applicationStore.attachments,
      this.applicationStore.pendingAttachments,
      (saved, pending) => saved.length + pending.length
    );
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

  private defaultAttachmentsForArea(applicationType: ApplicationType): Observable<Array<DefaultAttachmentInfo>> {
    if (this.applicationStore.isNew) {
      return this.mapHub.fixedLocationAreaBySectionIds(this.application.firstLocation.fixedLocationIds)
        .switchMap(area => this.attachmentHub.defaultAttachmentInfosByArea(applicationType, area.id));
    } else {
      return Observable.of([]);
    }
  }

  private sidebarItem(item: SidebarItem): Option<SidebarItem> {
    return Some(visibleFor(this.application.type, item.type))
      .filter(visible => visible)
      .map(visible => item);
  }
}
