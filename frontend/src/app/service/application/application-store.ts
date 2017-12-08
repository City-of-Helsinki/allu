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
import {Deposit} from '../../model/application/invoice/deposit';
import {DepositService} from './deposit/deposit.service';

export interface ApplicationState {
  application?: Application;
  tags?: Array<ApplicationTag>;
  pendingAttachments?: Array<AttachmentInfo>;
  attachments?: Array<AttachmentInfo>;
  comments?: Array<Comment>;
  tab?: SidebarItemType;
  relatedProject?: number;
  isCopy?: boolean;
  deposit?: Deposit;
}

const initialState: ApplicationState = {
  application: new Application(),
  tags: [],
  pendingAttachments: [],
  attachments: [],
  comments: [],
  tab: 'BASIC_INFO',
  relatedProject: undefined,
  isCopy: false,
  deposit: undefined
};

@Injectable()
export class ApplicationStore {
  private store = new BehaviorSubject<ApplicationState>(initialState);

  constructor(private applicationHub: ApplicationHub,
              private customerHub: CustomerHub,
              private attachmentHub: AttachmentHub,
              private commentHub: CommentHub,
              private depositService: DepositService) {
  }

  get snapshot(): ApplicationState {
    return this.store.getValue();
  }

  get changes(): Observable<ApplicationState> {
    return this.store.asObservable()
      .distinctUntilChanged();
  }

  reset(): void {
    this.store.next(initialState);
  }

  get application(): Observable<Application> {
    return this.store.map(change => change.application).distinctUntilChanged();
  }

  applicationChange(application: Application) {
    this.store.next({
      ...this.snapshot,
      application: application
    });
  }

  get isNew(): boolean {
    return this.snapshot.application.id === undefined;
  }

  set applicationCopy(application: Application) {
    this.store.next({
      ...this.snapshot,
      application: application,
      isCopy: true
    });
  }

  get tags(): Observable<Array<ApplicationTag>> {
    return this.store.map(change => change.tags).distinctUntilChanged();
  }

  changeTags(tags: Array<ApplicationTag>) {
    this.store.next({...this.snapshot, tags});
  }

  saveTags(tags: Array<ApplicationTag>) {
    const applicationId = this.snapshot.application.id;
    if (!NumberUtil.isDefined(applicationId)) {
      throw new Error('Cannot save tags when application state has no saved application');
    }

    return this.applicationHub.saveTags(applicationId, tags)
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
    this.store.next({...this.snapshot, tab});
  }

  get attachments(): Observable<Array<AttachmentInfo>> {
    return this.store.map(state => state.attachments).distinctUntilChanged();
  }

  get pendingAttachments(): Observable<Array<AttachmentInfo>> {
    return this.store.map(state => state.pendingAttachments).distinctUntilChanged();
  }

  get allAttachments(): Observable<Array<AttachmentInfo>> {
    return Observable.combineLatest(
      this.attachments,
      this.pendingAttachments,
      (saved, pending) => saved.concat(pending)
    );
  }

  addAttachment(attachment: AttachmentInfo) {
    const current = this.snapshot.pendingAttachments.slice();
    this.store.next({
      ...this.snapshot,
      pendingAttachments: current.concat(attachment)
    });
  }

  saveAttachment(applicationId: number, attachment: AttachmentInfo): Observable<AttachmentInfo> {
    return this.saveAttachments(applicationId, [attachment])
      .filter(attachments => attachments.length > 0)
      .map(attachments => attachments[0]);
  }

  removeAttachment(attachmentId: number, index?: number): Observable<HttpResponse> {
    if (attachmentId) {
      const appId = this.snapshot.application.id;
      return this.attachmentHub.remove(appId, attachmentId)
        .do(response => this.loadAttachments(appId).subscribe());
    } else {
      const pending = this.snapshot.pendingAttachments;
      Some(index).do(i => this.store.next({...this.snapshot, pendingAttachments: pending.splice(i, 1)}));
      return Observable.of(new HttpResponse(HttpStatus.ACCEPTED));
    }
  }

  loadAttachments(applicationId: number): Observable<Array<AttachmentInfo>> {
    return this.applicationHub.getAttachments(applicationId)
      .do(attachments => this.store.next({...this.snapshot, attachments}));
  }

  saveComment(applicationId: number, comment: Comment): Observable<Comment> {
    return this.commentHub.saveComment(applicationId, comment)
      .do(c => this.loadComments(this.snapshot.application.id).subscribe());
  }

  removeComment(commentId: number): Observable<HttpResponse> {
    return this.commentHub.removeComment(commentId)
      .do(c => this.loadComments(this.snapshot.application.id).subscribe());
  }

  loadComments(id: number): Observable<Array<Comment>> {
    return this.commentHub.getComments(id)
      .do(comments => this.store.next({...this.snapshot, comments}));
  }

  load(id: number): Observable<Application> {
    return this.applicationHub.getApplication(id)
      .do(application => {
        this.store.next({
          ...this.snapshot,
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
      .switchMap(app => this.savePending(app))
      .do(app => this.saved(app));
  }

  delete(id: number): Observable<HttpResponse> {
    return this.applicationHub.delete(id)
      .do(response => this.reset());
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const appId = id || this.snapshot.application.id;
    return this.applicationHub.changeStatus(appId, status, changeInfo)
      .do(application => this.applicationChange(application));
  }

  changeRelatedProject(projectId: number) {
    this.store.next({...this.snapshot, relatedProject: projectId});
  }

  changeIsCopy(isCopy: boolean) {
    this.store.next({...this.snapshot, isCopy});
  }

  loadDeposit(): Observable<Deposit> {
    const appId = this.snapshot.application.id;
    return this.depositService.fetchByApplication(appId)
      .do(deposit => this.store.next({...this.snapshot, deposit}));
  }

  get deposit(): Observable<Deposit> {
    return this.store.map(state => state.deposit).distinctUntilChanged();
  }

  saveDeposit(deposit: Deposit): Observable<Deposit> {
    return this.depositService.save(deposit)
      .do(saved => {
        this.load(deposit.applicationId).subscribe();
        this.store.next({...this.snapshot, deposit: saved});
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
    return this.applicationHub.save(application);
  }

  private savePending(application: Application) {
    return this.savePendingAttachments(application);
  }

  private savePendingAttachments(application: Application): Observable<Application> {
    const result = new Subject<Application>();

    this.saveAttachments(application.id, this.snapshot.pendingAttachments)
      .subscribe(
        items => { /* Nothing to do with saved items */ },
        error => result.error(error),
        () => {
          result.next(application);
          result.complete();
          this.store.next({...this.snapshot, pendingAttachments: []});
        });

    return result.asObservable();
  }

  private saved(application: Application): void {
    this.store.next({...this.snapshot, application, tags: application.applicationTags});
  }
}
