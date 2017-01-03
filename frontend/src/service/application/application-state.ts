import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ApplicationHub} from './application-hub';
import {Application} from '../../model/application/application';
import {LocationState} from './location-state';
import {Some} from '../../util/option';
import {ProjectHub} from '../project/project-hub';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentHub} from '../../feature/application/info/attachment/attachment-hub';
import {Subject} from 'rxjs';
import {HttpResponse} from '../../util/http.util';


@Injectable()
export class ApplicationState {
  private pendingAttachments: Array<AttachmentInfo> = [];

  constructor(private router: Router,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private locationState: LocationState,
              private attachmentHub: AttachmentHub) {}

  addAttachment(attachment: AttachmentInfo) {
    this.pendingAttachments.push(attachment);
  }

  saveAttachment(applicationId: number, attachment: AttachmentInfo): Observable<AttachmentInfo> {
    let result = new Subject<AttachmentInfo>();

    // TODO: use saved attachment as return value
    this.attachmentHub.upload(applicationId, [attachment])
      .subscribe(
        progress => console.log('Upload progress', progress),
        error => result.error(error),
        () =>  {
          result.next(attachment);
          result.complete();
        });

    return result;
  }

  removeAttachment(attachmentId: number): Observable<HttpResponse> {
    return this.attachmentHub.remove(attachmentId);
  }

  save(application: Application): Observable<Application> {
    return this.applicationHub.save(application)
      .switchMap(app => this.saveAttachments(app));
  }

  private saveAttachments(application: Application): Observable<Application> {
    let result = new Subject<Application>();

    this.attachmentHub.upload(application.id, this.pendingAttachments)
      .subscribe(
        progress => console.log('Upload progress', progress),
        error => result.error(error),
        () => {
          this.saved(application).subscribe(app => result.next(app));
          result.complete();
          this.pendingAttachments = [];
        });

    return result;
  }

  private saved(application: Application): Observable<Application> {
    console.log('Application saved');
    this.locationState.clear();
    // We had related project so navigate back to project page
    Some(this.locationState.relatedProject)
      .do(projectId => this.projectHub.addProjectApplication(projectId, application.id).subscribe(project =>
        this.router.navigate(['/projects', project.id])));

    this.router.navigate(['applications', application.id, 'summary']);
    return Observable.of(application);
  }
}
