import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {UrlUtil} from '../../../util/url.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {SidebarItem, SidebarItemType, visibleFor, visibleItemsByApplicationType} from '../../sidebar/sidebar-item';
import {ProgressStep, stepFrom} from '../progressbar/progress-step';
import {ApplicationStatus} from '../../../model/application/application-status';
import {AttachmentHub} from '../attachment/attachment-hub';
import {MapHub} from '../../../service/map/map-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';
import {Option, Some} from '../../../util/option';

@Component({
  selector: 'application',
  viewProviders: [],
  template: require('./application.component.html'),
  styles: [
    require('./application.component.scss')
  ]
})
export class ApplicationComponent implements OnInit {
  progressStep: ProgressStep;
  application: Application;
  readonly: boolean;
  sidebarItems: Array<SidebarItem> = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private applicationState: ApplicationState,
              private attachmentHub: AttachmentHub,
              private mapHub: MapHub) {
  }

  ngOnInit(): void {
    this.application = this.applicationState.application;
    this.verifyTypeExists(ApplicationType[this.application.type]);

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.readonly = summary;
      this.progressStep = stepFrom(ApplicationStatus[this.application.status], summary);

      this.defaultAttachmentsForArea(this.application.typeEnum).subscribe(
        attachments => attachments.forEach(a => this.applicationState.addAttachment(a)),
        err => NotificationService.errorMessage(findTranslation('attachment.error.defaultAttachmentByArea')));

      this.sidebar(summary).subscribe(
        items => this.sidebarItems = items,
        err => console.log('Failed to load sidebar statistics'));
    });
  }

  verifyTypeExists(type: ApplicationType) {
    if (type === undefined) {
      // No known type so navigate back to type selection
      this.router.navigateByUrl('applications/location');
    }
  }

  onTagChange(tags: Array<ApplicationTag>): void {
    this.applicationState.tags = tags;
  }

  sidebar(summary: boolean): Observable<Array<SidebarItem>> {
    return Observable.combineLatest(
      this.attachmentCount(),
      this.commentCount(),
      this.createSidebar(summary));
  }

  private attachmentCount(): Observable<number> {
    return Observable.combineLatest(
      this.applicationState.attachments,
      this.applicationState.pendingAttachments,
      (saved, pending) => saved.length + pending.length
    );
  }

  private commentCount(): Observable<number> {
    return this.applicationState.comments
      .map(comments => comments.length);
  }

  private createSidebar(summary: boolean): (a: number, c: number) => Array<SidebarItem> {
    return (attachmentCount: number, commentCount: number) => {
      let sidebar: Array<SidebarItem> = [
        { type: 'BASIC_INFO'},
        { type: 'ATTACHMENTS', count: attachmentCount }
      ];

      if (summary) {
        this.sidebarItem('COMMENTS', commentCount).do(item => sidebar.push(item));
        this.sidebarItem('HISTORY').do(item => sidebar.push(item));
        this.sidebarItem('DECISION').do(item => sidebar.push(item));
        this.sidebarItem('INVOICING').do(item => sidebar.push(item));
      }
      return sidebar;
    };
  }

  private defaultAttachmentsForArea(applicationType: ApplicationType): Observable<Array<DefaultAttachmentInfo>> {
    if (this.applicationState.isNew) {
      return this.mapHub.fixedLocationAreaBySectionIds(this.application.firstLocation.fixedLocationIds)
        .switchMap(area => this.attachmentHub.defaultAttachmentInfosByArea(applicationType, area.id));
    } else {
      return Observable.of([]);
    }
  }

  private sidebarItem(type: SidebarItemType, count?: number): Option<SidebarItem> {
    return Some(visibleFor(this.application.type, type))
      .filter(visible => visible)
      .map(visible => { return {type: type, count: count}; });
  }
}
