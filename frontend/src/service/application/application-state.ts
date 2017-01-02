import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ApplicationHub} from './application-hub';
import {Application} from '../../model/application/application';
import {LocationState} from './location-state';
import {Some} from '../../util/option';
import {ProjectHub} from '../project/project-hub';
import {AttachmentInfo} from '../../model/application/attachment-info';
import {ApplicationAttachmentHub} from '../../feature/application/info/attachment/application-attachment-hub';


@Injectable()
export class ApplicationState {
  private _attachments: Array<AttachmentInfo>;

  constructor(private router: Router,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private locationState: LocationState,
              private attachmentHub: ApplicationAttachmentHub) {}

  set attachments(attachments: Array<AttachmentInfo>) {
    this._attachments = attachments;
  }

  save(application: Application): Observable<Application> {
    return this.applicationHub.save(application)
      .switchMap(app => this.saveAttachments(app));
  }

  private saveAttachments(application: Application): Observable<Application> {
    let result: Observable<Application> = Observable.empty();

    this.attachmentHub.upload(application.id, this._attachments)
      .subscribe(
        progress => console.log('Upload progress', progress),
        error => result = error,
        () => result = this.saved(application));

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
