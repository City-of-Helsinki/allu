import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
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
import {Deposit} from '../../model/application/invoice/deposit';
import {DepositService} from './deposit/deposit.service';
import {ApplicationService} from './application.service';
import {isCommon} from '../../model/application/attachment/attachment-type';

export interface ApplicationState {
  application?: Application;
  applicationCopy?: Application;
  tags?: Array<ApplicationTag>;
  attachments?: Array<AttachmentInfo>;
  comments?: Array<Comment>;
  tab?: SidebarItemType;
  relatedProject?: number;
  deposit?: Deposit;
}

export const initialState: ApplicationState = {
  application: new Application(),
  applicationCopy: undefined,
  tags: [],
  attachments: [],
  comments: [],
  tab: 'BASIC_INFO',
  relatedProject: undefined,
  deposit: undefined
};

@Injectable()
export class ApplicationStore {
  private store = new BehaviorSubject<ApplicationState>(initialState);

  constructor(private applicationService: ApplicationService,
              private customerHub: CustomerHub,
              private attachmentHub: AttachmentHub,
              private commentHub: CommentHub,
              private depositService: DepositService) {
  }

  get snapshot(): ApplicationState {
    return ObjectUtil.clone(this.current);
  }

  get changes(): Observable<ApplicationState> {
    return this.store.asObservable()
      .distinctUntilChanged();
  }

  reset(): void {
    this.store.next(initialState);
  }

  get application(): Observable<Application> {
    return this.store.map(change => change.application)
      .distinctUntilChanged()
      .filter(app => !!app);
  }

  applicationChange(application: Application) {
    this.store.next({
      ...this.current,
      application: application
    });
  }

  get isNew(): boolean {
    return this.current.application.id === undefined;
  }

  applicationCopyChange(application: Application) {
    this.store.next({
      ...this.current,
      applicationCopy: ObjectUtil.clone(application)
    });
  }

  currentOrCopy(): Application {
    const copy = this.snapshot.applicationCopy;
    if (copy) {
      this.store.next({
        ...initialState,
        application: copy
      });
    }
    return this.snapshot.application;
  }

  replace(): Observable<Application> {
    return this.applicationService.replace(this.current.application.id)
      .do(replacement => this.applicationChange(replacement));
  }

  get tags(): Observable<Array<ApplicationTag>> {
    return this.store.map(change => change.tags).distinctUntilChanged();
  }

  changeTags(tags: Array<ApplicationTag>) {
    this.store.next({...this.current, tags});
  }

  saveTags(tags: Array<ApplicationTag>) {
    const applicationId = this.current.application.id;
    if (!NumberUtil.isDefined(applicationId)) {
      throw new Error('Cannot save tags when application state has no saved application');
    }

    return this.applicationService.saveTags(applicationId, tags)
      .do(savedTags => this.changeTags(savedTags));
  }

  get comments(): Observable<Array<Comment>> {
    return this.store.map(state => state.comments).distinctUntilChanged();
  }

  get tab(): Observable<SidebarItemType> {
    return this.store.map(state => state.tab)
      .skip(1)
      .distinctUntilChanged();
  }

  changeTab(tab: SidebarItemType): void {
    this.store.next({...this.current, tab});
  }

  get attachments(): Observable<Array<AttachmentInfo>> {
    return this.store.map(state => state.attachments).distinctUntilChanged();
  }

  saveAttachment(attachment: AttachmentInfo): Observable<AttachmentInfo> {
    if (NumberUtil.isDefined(attachment.id) && isCommon(attachment.type)) {
      return this.updateAttachment(attachment);
    } else {
      return this.saveAttachments(this.snapshot.application.id, [attachment])
        .filter(attachments => attachments.length > 0)
        .map(attachments => attachments[0]);
    }
  }

  removeAttachment(attachmentId: number): Observable<HttpResponse> {
    const appId = this.snapshot.application.id;
    return this.attachmentHub.remove(appId, attachmentId)
      .do(response => this.loadAttachments(appId).subscribe());
  }

  loadAttachments(applicationId: number): Observable<Array<AttachmentInfo>> {
    return this.applicationService.getAttachments(applicationId)
      .do(attachments => this.store.next({...this.current, attachments}));
  }

  saveComment(applicationId: number, comment: Comment): Observable<Comment> {
    return this.commentHub.saveComment(applicationId, comment)
      .do(c => this.loadComments(this.current.application.id).subscribe());
  }

  removeComment(commentId: number): Observable<HttpResponse> {
    return this.commentHub.removeComment(commentId)
      .do(c => this.loadComments(this.current.application.id).subscribe());
  }

  loadComments(id: number): Observable<Array<Comment>> {
    return this.commentHub.getComments(id)
      .do(comments => this.store.next({...this.current, comments}));
  }

  load(id: number): Observable<Application> {
    return this.applicationService.get(id)
      .do(application => {
        this.store.next({
          ...this.current,
          application,
          attachments: application.attachmentList,
          tags: application.applicationTags,
          comments: application.comments
        });
      });
  }

  save(application: Application): Observable<Application> {
    return this.saveCustomersAndContacts(application)
      .switchMap(app => this.saveApplication(app))
      .do(app => this.saved(app));
  }

  delete(id: number): Observable<HttpResponse> {
    return this.applicationService.remove(id)
      .do(response => this.reset());
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const appId = id || this.snapshot.application.id;
    return this.applicationService.changeStatus(appId, status, changeInfo)
      .do(application => this.applicationChange(application));
  }

  changeRelatedProject(projectId: number) {
    this.store.next({...this.current, relatedProject: projectId});
  }

  loadDeposit(): Observable<Deposit> {
    const appId = this.snapshot.application.id;
    return this.depositService.fetchByApplication(appId)
      .do(deposit => this.store.next({...this.current, deposit}));
  }

  get deposit(): Observable<Deposit> {
    return this.store.map(state => state.deposit).distinctUntilChanged();
  }

  saveDeposit(deposit: Deposit): Observable<Deposit> {
    return this.depositService.save(deposit)
      .do(saved => {
        this.load(deposit.applicationId).subscribe();
        this.store.next({...this.current, deposit: saved});
      });
  }

  private saveAttachments(applicationId: number, attachments: Array<AttachmentInfo>): Observable<Array<AttachmentInfo>> {
    if (attachments.length === 0) {
      return this.loadAttachments(applicationId);
    } else {
      const result = new Subject<Array<AttachmentInfo>>();
      this.attachmentHub.upload(applicationId, attachments)
        .subscribe(
          items => result.next(items),
          error => result.error(error),
          () => result.complete());

      return result.do(a => this.loadAttachments(applicationId).subscribe());
    }
  }

  private updateAttachment(attachment: AttachmentInfo): Observable<AttachmentInfo> {
    return this.attachmentHub.update(attachment)
      .do(a => this.loadAttachments(this.snapshot.application.id));
  }

  private saveCustomersAndContacts(application: Application): Observable<Application> {
    const app = ObjectUtil.clone(application);
    return Observable.forkJoin(application.customersWithContacts.map(cwc =>
      this.customerHub.saveCustomerWithContacts(cwc))
    ).map(savedCustomersWithContacts => {
      app.customersWithContacts = savedCustomersWithContacts;
      return app;
    });
  }

  private saveApplication(application: Application): Observable<Application> {
    application.applicationTags = this.snapshot.tags;
    return this.applicationService.save(application);
  }

  private saved(application: Application): void {
    this.store.next({...this.current, application, tags: application.applicationTags});
  }

  private get current(): ApplicationState {
    return this.store.getValue();
  }
}
