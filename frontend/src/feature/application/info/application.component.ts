import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {UrlUtil} from '../../../util/url.util';
import {ApplicationType} from '../../../model/application/type/application-type';
import {Application} from '../../../model/application/application';
import {ApplicationState} from '../../../service/application/application-state';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {SidebarItem} from '../../sidebar/sidebar-item';
import {ProgressStep, stepFrom} from '../progressbar/progress-step';
import {ApplicationStatus} from '../../../model/application/application-status';


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

  constructor(private route: ActivatedRoute, private router: Router, private applicationState: ApplicationState) {
  }

  ngOnInit(): void {
    this.application = this.applicationState.application;
    this.verifyTypeExists(ApplicationType[this.application.type]);

    UrlUtil.urlPathContains(this.route, 'summary').forEach(summary => {
      this.readonly = summary;

      this.progressStep = stepFrom(ApplicationStatus[this.application.status], summary);
      this.sidebar(summary).subscribe(items => this.sidebarItems = items);
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
    return this.applicationState.attachments
      .map(attachments => attachments.length + this.applicationState.pendingAttachments.length);
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
        sidebar.push({type: 'COMMENTS', count: commentCount});
        sidebar.push({type: 'HISTORY'});
      }
      return sidebar;
    };
  }
}
