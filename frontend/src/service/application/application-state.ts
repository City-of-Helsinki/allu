import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ApplicationHub} from './application-hub';
import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ProjectHub} from '../project/project-hub';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentHub} from '../../feature/application/attachment/attachment-hub';
import {Subject} from 'rxjs';
import {HttpResponse, HttpStatus} from '../../util/http.util';
import {Comment} from '../../model/application/comment/comment';
import {CommentHub} from './comment/comment-hub';


@Injectable()
export class ApplicationState {
  public relatedProject: number;

  private _application = new Application();
  private _pendingAttachments: Array<AttachmentInfo> = [];

  constructor(private router: Router,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private attachmentHub: AttachmentHub,
              private commentHub: CommentHub) {
  }

  get application(): Application {
    return this._application;
  }

  set application(value: Application) {
    this._application = value;
  }

  get pendingAttachments(): Array<AttachmentInfo> {
    return this._pendingAttachments;
  }

  addAttachment(attachment: AttachmentInfo) {
    this._pendingAttachments.push(attachment);
  }

  saveAttachment(applicationId: number, attachment: AttachmentInfo): Observable<AttachmentInfo> {
    let result = new Subject<AttachmentInfo>();

    this.attachmentHub.upload(applicationId, [attachment])
      .subscribe(
        items => result.next(items[0]),
        error => result.error(error),
        () => result.complete());

    return result;
  }

  removeAttachment(index: number, attachmentId: number): Observable<HttpResponse> {
    if (attachmentId) {
      return this.attachmentHub.remove(attachmentId);
    } else {
      this._pendingAttachments.splice(index, 1);
      return Observable.of(new HttpResponse(HttpStatus.ACCEPTED));
    }
  }

  saveComment(applicationId: number, comment: Comment): Observable<Comment> {
    return this.commentHub.saveComment(applicationId, comment);
  }

  removeComment(comment: Comment): Observable<HttpResponse> {
    return this.commentHub.removeComment(comment.id);
  }

  load(id: number): Observable<Application> {
    return this.applicationHub.getApplication(id)
      .do(app => this.application = app);
  }

  save(application: Application): Observable<Application> {
    return this.applicationHub.save(application)
      .switchMap(app => this.saveAttachments(app));
  }

  private saveAttachments(application: Application): Observable<Application> {
    let result = new Subject<Application>();

    this.attachmentHub.upload(application.id, this._pendingAttachments)
      .subscribe(
        items => { /* Nothing to do with saved items */ },
        error => result.error(error),
        () => {
          this.saved(application).subscribe(app => result.next(app));
          result.complete();
          this._pendingAttachments = [];
        });

    return result;
  }

  private saved(application: Application): Observable<Application> {
    console.log('Application saved');
    this.application = application;
    // We had related project so navigate back to project page
    Some(this.relatedProject)
      .do(projectId => this.projectHub.addProjectApplication(projectId, application.id).subscribe(project =>
        this.router.navigate(['/projects', project.id])));

    this.router.navigate(['applications', application.id, 'summary']);
    return Observable.of(application);
  }
}
