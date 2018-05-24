
import {throwError as observableThrowError, Observable, Subject, BehaviorSubject, forkJoin} from 'rxjs';
import {Injectable} from '@angular/core';
import {Application} from '../../model/application/application';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentHub} from '../../feature/application/attachment/attachment-hub';
import {Comment} from '../../model/application/comment/comment';
import {ApplicationTag} from '../../model/application/tag/application-tag';
import {SidebarItemType} from '../../feature/sidebar/sidebar-item';
import {NumberUtil} from '../../util/number.util';
import {ObjectUtil} from '../../util/object.util';
import {ApplicationStatus} from '../../model/application/application-status';
import {StatusChangeInfo} from '../../model/application/status-change-info';
import {Deposit} from '../../model/application/invoice/deposit';
import {DepositService} from './deposit/deposit.service';
import {ApplicationService} from './application.service';
import {isCommon} from '../../model/application/attachment/attachment-type';
import {ApplicationDraftService} from './application-draft.service';
import {CustomerService} from '../customer/customer.service';
import {CommentService} from './comment/comment.service';
import {catchError, distinctUntilChanged, filter, map, skip, switchMap, tap} from 'rxjs/internal/operators';

export interface ApplicationState {
  application?: Application;
  applicationCopy?: Application;
  tags?: Array<ApplicationTag>;
  attachments?: Array<AttachmentInfo>;
  tab?: SidebarItemType;
  relatedProject?: number;
  deposit?: Deposit;
  draft?: boolean;
  processing?: boolean;
}

export const initialState: ApplicationState = {
  application: new Application(),
  applicationCopy: undefined,
  tags: [],
  attachments: [],
  tab: 'BASIC_INFO',
  relatedProject: undefined,
  deposit: undefined,
  draft: false,
  processing: false
};

@Injectable()
export class ApplicationStore {
  private store = new BehaviorSubject<ApplicationState>(initialState);

  constructor(private applicationService: ApplicationService,
              private applicationDraftService: ApplicationDraftService,
              private customerService: CustomerService,
              private attachmentHub: AttachmentHub,
              private depositService: DepositService) {
  }

  get snapshot(): ApplicationState {
    return ObjectUtil.clone(this.current);
  }

  get changes(): Observable<ApplicationState> {
    return this.store.asObservable().pipe(distinctUntilChanged());
  }

  reset(): void {
    this.store.next(initialState);
  }

  get application(): Observable<Application> {
    return this.store.pipe(
      map(change => change.application),
      distinctUntilChanged(),
      filter(app => !!app)
    );
  }

  applicationChange(application: Application) {
    this.store.next({
      ...this.current,
      application: application,
      processing: false
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
    return this.applicationService.replace(this.current.application.id).pipe(
      tap(replacement => this.applicationChange(replacement))
    );
  }

  get tags(): Observable<Array<ApplicationTag>> {
    return this.store.pipe(
      map(change => change.tags),
      distinctUntilChanged()
    );
  }

  changeTags(tags: Array<ApplicationTag>) {
    this.store.next({...this.current, tags});
  }

  saveTags(tags: Array<ApplicationTag>) {
    const applicationId = this.current.application.id;
    if (!NumberUtil.isDefined(applicationId)) {
      throw new Error('Cannot save tags when application state has no saved application');
    }

    return this.applicationService.saveTags(applicationId, tags).pipe(
      tap(savedTags => this.changeTags(savedTags))
    );
  }

  changeDraft(draft: boolean) {
    this.store.next({...this.current, draft});
  }

  get tab(): Observable<SidebarItemType> {
    return this.store.pipe(
      map(state => state.tab),
      skip(1),
      distinctUntilChanged()
    );
  }

  changeTab(tab: SidebarItemType): void {
    this.store.next({...this.current, tab});
  }

  get attachments(): Observable<Array<AttachmentInfo>> {
    return this.store.pipe(
      map(state => state.attachments),
      distinctUntilChanged()
    );
  }

  saveAttachment(attachment: AttachmentInfo): Observable<AttachmentInfo> {
    if (NumberUtil.isDefined(attachment.id) && isCommon(attachment.type)) {
      return this.updateAttachment(attachment);
    } else {
      return this.saveAttachments(this.snapshot.application.id, [attachment]).pipe(
        filter(attachments => attachments.length > 0),
        map(attachments => attachments[0])
      );
    }
  }

  removeAttachment(attachmentId: number): Observable<{}> {
    const appId = this.snapshot.application.id;
    return this.attachmentHub.remove(appId, attachmentId).pipe(
      tap(response => this.loadAttachments(appId).subscribe())
    );
  }

  loadAttachments(applicationId: number): Observable<Array<AttachmentInfo>> {
    return this.applicationService.getAttachments(applicationId).pipe(
      tap(attachments => this.store.next({...this.current, attachments}))
    );
  }

  load(id: number): Observable<Application> {
    return this.applicationService.get(id).pipe(
      tap(application => {
        this.store.next({
          ...this.current,
          application,
          attachments: application.attachmentList,
          tags: application.applicationTags,
          draft: application.statusEnum === ApplicationStatus.PRE_RESERVED
        });
      })
    );
  }

  save(application: Application): Observable<Application> {
    return this.saveCustomersAndContacts(application).pipe(
      switchMap(app => this.saveApplication(app)),
      tap(app => this.saved(app))
    );
  }

  delete(id: number): Observable<{}> {
    const response = this.snapshot.draft
      ? this.applicationDraftService.remove(id)
      : this.applicationService.remove(id);

    return response.pipe(tap(() => this.reset()));
  }

  changeStatus(id: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const appId = id || this.snapshot.application.id;
    this.store.next({...this.current, processing: true});
    return this.applicationService.changeStatus(appId, status, changeInfo).pipe(
      tap(application => this.applicationChange(application)),
      catchError(err => {
        this.store.next({...this.current, processing: false});
        return observableThrowError(err);
      })
    );
  }

  changeRelatedProject(projectId: number) {
    this.store.next({...this.current, relatedProject: projectId});
  }

  loadDeposit(): Observable<Deposit> {
    const appId = this.snapshot.application.id;
    return this.depositService.fetchByApplication(appId).pipe(
      tap(deposit => this.store.next({...this.current, deposit}))
    );
  }

  get deposit(): Observable<Deposit> {
    return this.store.pipe(
      map(state => state.deposit),
      distinctUntilChanged()
    );
  }

  saveDeposit(deposit: Deposit): Observable<Deposit> {
    return this.depositService.save(deposit).pipe(
      tap(saved => {
        this.load(deposit.applicationId).subscribe();
        this.store.next({...this.current, deposit: saved});
      })
    );
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

      return result.pipe(tap(a => this.loadAttachments(applicationId).subscribe()));
    }
  }

  private updateAttachment(attachment: AttachmentInfo): Observable<AttachmentInfo> {
    return this.attachmentHub.update(attachment).pipe(
      tap(a => this.loadAttachments(this.snapshot.application.id))
    );
  }

  private saveCustomersAndContacts(application: Application): Observable<Application> {
    const app = ObjectUtil.clone(application);
    return forkJoin(application.customersWithContacts.map(cwc =>
      this.customerService.saveCustomerWithContacts(cwc))
    ).pipe(
      map(savedCustomersWithContacts => {
        app.customersWithContacts = savedCustomersWithContacts;
        return app;
      })
    );
  }

  private saveApplication(application: Application): Observable<Application> {
    const snapshot = this.snapshot;
    application.applicationTags = snapshot.tags;
    if (snapshot.draft) {
      return this.applicationDraftService.save(application);
    } else {
      // Convert to full application
      if (application.statusEnum === ApplicationStatus.PRE_RESERVED) {
        return this.applicationDraftService.convertToApplication(application);
      } else {
        return this.applicationService.save(application);
      }
    }
  }

  private saved(application: Application): void {
    this.store.next({...this.current, application, tags: application.applicationTags});
  }

  private get current(): ApplicationState {
    return this.store.getValue();
  }
}
