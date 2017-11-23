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
import {NumberUtil} from '../../util/number.util';
import {ObjectUtil} from '../../util/object.util';
import {CustomerHub} from '../customer/customer-hub';
import {ApplicationStatus} from '../../model/application/application-status';
import {StatusChangeInfo} from '../../model/application/status-change-info';

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
              private customerHub: CustomerHub,
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

  get changes(): Observable<Application> {
    return this.application$.asObservable();
  }

  set tags(tags: Array<ApplicationTag>) {
    const app = this.application;
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

  get allAttachments(): Observable<Array<AttachmentInfo>> {
    return Observable.combineLatest(
      this.attachments,
      this.pendingAttachments,
      (saved, pending) => saved.concat(pending)
    );
  }

  /**
   * Observable for comments other than supervision
   */
  get comments(): Observable<Array<Comment>> {
    return this.comments$.asObservable();
  }

  get tabChange(): Observable<SidebarItemType> {
    return this.tabChange$.asObservable();
  }

  addAttachment(attachment: AttachmentInfo) {
    const current = this.pendingAttachments$.getValue();
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
      const result = new Subject<Array<AttachmentInfo>>();
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
      const pending = this.pendingAttachments$.getValue();
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

  removeComment(commentId: number): Observable<HttpResponse> {
    return this.commentHub.removeComment(commentId)
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
    return this.saveCustomersAndContacts(application)
      .switchMap(app => this.applicationHub.save(app))
      .switchMap(app => this.savePending(app))
      .switchMap(app => this.saved(app));
  }

  delete(id: number): Observable<HttpResponse> {
    return this.applicationHub.delete(id)
      .do(response => this.reset());
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const appId = id || this.application.id;
    return this.applicationHub.changeStatus(appId, status, changeInfo)
      .do(application => this.application = application);
  }

  saveTags(tags: Array<ApplicationTag>) {
    if (!NumberUtil.isDefined(this.application.id)) {
      throw new Error('Cannot save tags when application state has no saved application');
    }

    return this.applicationHub.saveTags(this.application.id, tags)
      .map(savedTags => {
        const app = ObjectUtil.clone(this.application);
        app.applicationTags = savedTags;
        return app;
      }).do(app => this.application$.next(app));
  }

  private saveCustomersAndContacts(application: Application): Observable<Application> {
    return Observable.forkJoin(application.customersWithContacts.map(cwc =>
      this.customerHub.saveCustomerWithContacts(cwc))
    ).map(savedCustomersWithContacts => {
      application.customersWithContacts = savedCustomersWithContacts;
      return application;
    });
  }

  private savePending(application: Application) {
    return this.savePendingAttachments(application);
  }

  private savePendingAttachments(application: Application): Observable<Application> {
    const result = new Subject<Application>();

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
