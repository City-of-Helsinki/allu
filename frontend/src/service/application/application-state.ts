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
import {Comment} from '../../model/application/comment/comment';
import {CommentHub} from './comment/comment-hub';
import {ApplicationTag} from '../../model/application/tag/application-tag';
import {SidebarItemType} from '../../feature/sidebar/sidebar-item';
import {HttpResponse, HttpStatus} from '../../util/http-response';

@Injectable()
export class ApplicationState {
  public relatedProject: number;
  public isCopy = false;

  private application$ = new BehaviorSubject<Application>(new Application());
  private pendingAttachments$ = new BehaviorSubject<Array<AttachmentInfo>>([]);
  private attachments$ = new BehaviorSubject<Array<AttachmentInfo>>([]);
  private comments$ = new BehaviorSubject<Array<Comment>>([]);
  private tabChange$ = new Subject<SidebarItemType>();

  constructor(private router: Router,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private attachmentHub: AttachmentHub,
              private commentHub: CommentHub) {
  }

  reset(): void {
    this.application$.next(new Application());
    this.pendingAttachments$.next([]);
    this.attachments$.next([]);
    this.comments$.next([]);
    this.relatedProject = undefined;
    this.isCopy = false;
  }

  get application(): Application {
    return this.application$.getValue();
  }

  set application(value: Application) {
    this.application$.next(value);
  }

  get isNew(): boolean {
    return this.application.id === undefined;
  }

  set applicationCopy(app: Application) {
    this.application = app;
    this.isCopy = true;
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

  get pendingAttachments(): Observable<Array<AttachmentInfo>> {
    return this.pendingAttachments$.asObservable();
  }

  get allAttachmentsSnapshot(): Array<AttachmentInfo> {
    let pending = this.pendingAttachments$.getValue();
    let saved = this.attachments$.getValue();
    return pending.concat(saved);
  }

  get allAttachments(): Observable<Array<AttachmentInfo>> {
    return Observable.combineLatest(
      this.attachments,
      this.pendingAttachments,
      (saved, pending) => saved.concat(pending)
    );
  }

  get comments(): Observable<Array<Comment>> {
    return this.comments$.asObservable();
  }

  get tabChange(): Observable<SidebarItemType> {
    return this.tabChange$.asObservable();
  }

  addAttachment(attachment: AttachmentInfo) {
    let current = this.pendingAttachments$.getValue();
    this.pendingAttachments$.next(current.concat(attachment));
  }

  saveAttachment(applicationId: number, attachment: AttachmentInfo): Observable<AttachmentInfo> {
    return this.saveAttachments(applicationId, [attachment])
      .filter(attachments => attachments.length > 0)
      .map(attachments => attachments[0]);

  }

  saveAttachments(applicationId: number, attachments: Array<AttachmentInfo>): Observable<Array<AttachmentInfo>> {
    if (attachments.length === 0) {
      return this.loadAttachments(applicationId);
    } else {
      let result = new Subject<Array<AttachmentInfo>>();
      this.attachmentHub.upload(applicationId, attachments)
        .subscribe(
          items => result.next(items),
          error => result.error(error),
          () => result.complete());

      return result.switchMap(saved => this.loadAttachments(applicationId));
    }
  }

  removeAttachment(attachmentId: number, index?: number): Observable<HttpResponse> {
    if (attachmentId) {
      return this.attachmentHub.remove(this.application.id, attachmentId)
        .do(response => this.loadAttachments(this.application.id).subscribe());
    } else {
      let pending = this.pendingAttachments$.getValue();
      Some(index).do(i => this.pendingAttachments$.next(pending.splice(i, 1)));
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
      .switchMap(app => this.savePending(app))
      .switchMap(app => this.saved(app));
  }

  private savePending(application: Application) {
    return this.savePendingAttachments(application);
  }

  private savePendingAttachments(application: Application): Observable<Application> {
    let result = new Subject<Application>();

    this.saveAttachments(application.id, this.pendingAttachments$.getValue())
      .subscribe(
        items => { /* Nothing to do with saved items */ },
        error => result.error(error),
        () => {
          result.next(application);
          result.complete();
          this.pendingAttachments$.next([]);
        });

    return result.asObservable();
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
