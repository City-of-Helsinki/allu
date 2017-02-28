import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {ApplicationHub} from './application-hub';
import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ProjectHub} from '../project/project-hub';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentHub} from '../../feature/application/attachment/attachment-hub';
import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpResponse, HttpStatus} from '../../util/http.util';
import {Comment} from '../../model/application/comment/comment';
import {CommentHub} from './comment/comment-hub';
import {ApplicationTag} from '../../model/application/tag/application-tag';
import {SidebarItemType} from '../../feature/sidebar/sidebar-item';


@Injectable()
export class ApplicationState {
  public relatedProject: number;

  private application$ = new BehaviorSubject<Application>(new Application());
  private _pendingAttachments: Array<AttachmentInfo> = [];
  private attachments$ = new BehaviorSubject<Array<AttachmentInfo>>([]);
  private comments$ = new BehaviorSubject<Array<Comment>>([]);
  private tabChange$ = new Subject<SidebarItemType>();

  constructor(private router: Router,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private attachmentHub: AttachmentHub,
              private commentHub: CommentHub) {
  }

  get application(): Application {
    return this.application$.getValue();
  }

  set application(value: Application) {
    this.application$.next(value);
  }

  get applicationChanges(): Observable<Application> {
    return this.application$.asObservable();
  }

  set tags(tags: Array<ApplicationTag>) {
    let app = this.application;
    app.applicationTags = tags;
    this.application = app;
  }

  get tags(): Array<ApplicationTag> {
    return this.application.applicationTags;
  }

  get attachments(): Observable<Array<AttachmentInfo>> {
    return this.attachments$.asObservable();
  }

  get pendingAttachments(): Array<AttachmentInfo> {
    return this._pendingAttachments;
  }

  get comments(): Observable<Array<Comment>> {
    return this.comments$.asObservable();
  }

  get tabChange(): Observable<SidebarItemType> {
    return this.tabChange$.asObservable();
  }

  addAttachment(attachment: AttachmentInfo) {
    this._pendingAttachments.push(attachment);
  }

  saveAttachment(applicationId: number, attachment: AttachmentInfo): Observable<AttachmentInfo> {
    return this.saveAttachments(applicationId, [attachment])
      .filter(attachments => attachments.length > 0)
      .map(attachments => attachments[0]);

  }

  saveAttachments(applicationId: number, attachments: Array<AttachmentInfo>): Observable<Array<AttachmentInfo>> {
    let result = new Subject<Array<AttachmentInfo>>();

    this.attachmentHub.upload(applicationId, attachments)
      .subscribe(
        items => result.next(items),
        error => result.error(error),
        () => result.complete());

    return result.do(saved => this.loadAttachments(applicationId).subscribe());
  }

  removeAttachment(attachmentId: number, index?: number): Observable<HttpResponse> {
    if (attachmentId) {
      return this.attachmentHub.remove(this.application.id, attachmentId)
        .do(response => this.loadAttachments(this.application.id).subscribe());
    } else {
      Some(index).do(i => this._pendingAttachments.splice(i, 1));
      return Observable.of(new HttpResponse(HttpStatus.ACCEPTED));
    }
  }

  loadAttachments(id: number): Observable<Array<AttachmentInfo>> {
    return this.applicationHub.getApplication(id)
      .map(app => app.attachmentList)
      .do(attachments => this.attachments$.next(attachments));
  }

  saveComment(applicationId: number, comment: Comment): Observable<Comment> {
    return this.commentHub.saveComment(applicationId, comment)
      .do(c => this.loadComments(this.application.id).subscribe());
  }

  removeComment(comment: Comment): Observable<HttpResponse> {
    return this.commentHub.removeComment(comment.id)
      .do(c => this.loadComments(this.application.id).subscribe());
  }

  loadComments(id: number): Observable<Array<Comment>> {
    return this.commentHub.getComments(id)
      .do(comments => this.comments$.next(comments));
  }

  load(id: number): Observable<Application> {
    return this.applicationHub.getApplication(id)
      .do(app => {
        this.attachments$.next(app.attachmentList);
        this.application = app;
      });
  }

  notifyTabChange(tab: SidebarItemType): void {
    this.tabChange$.next(tab);
  }

  save(application: Application): Observable<Application> {
    return this.applicationHub.save(application)
      .switchMap(app => this.savePendingAttachments(app));
  }

  private savePendingAttachments(application: Application): Observable<Application> {
    let result = new Subject<Application>();
    this.saveAttachments(application.id, this._pendingAttachments)
      .subscribe(items => { /* Nothing to do with saved items */ },
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
