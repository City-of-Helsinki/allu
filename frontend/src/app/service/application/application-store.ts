import {BehaviorSubject, forkJoin, Observable, Subject, throwError as observableThrowError} from 'rxjs';
import {Injectable} from '@angular/core';
import {Application} from '../../model/application/application';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentHub} from '../../feature/application/attachment/attachment-hub';
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
import {catchError, distinctUntilChanged, filter, map, skip, switchMap, take, tap} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromApplication from '../../feature/application/reducers';
import {Load as LoadTags, LoadSuccess} from '../../feature/application/actions/application-tag-actions';

export interface ApplicationState {
  application?: Application;
  applicationCopy?: Application;
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
  attachments: [],
  tab: 'BASIC_INFO',
  relatedProject: undefined,
  deposit: undefined,
  draft: false,
  processing: false
};

@Injectable()
export class ApplicationStore {
  private appStore = new BehaviorSubject<ApplicationState>(initialState);

  constructor(private applicationService: ApplicationService,
              private applicationDraftService: ApplicationDraftService,
              private customerService: CustomerService,
              private attachmentHub: AttachmentHub,
              private depositService: DepositService,
              private store: Store<fromApplication.State>) {
  }

  get snapshot(): ApplicationState {
    return ObjectUtil.clone(this.current);
  }

  get changes(): Observable<ApplicationState> {
    return this.appStore.asObservable().pipe(distinctUntilChanged());
  }

  reset(): void {
    this.appStore.next(initialState);
  }

  get application(): Observable<Application> {
    return this.appStore.pipe(
      map(change => change.application),
      distinctUntilChanged(),
      filter(app => !!app)
    );
  }

  applicationChange(application: Application) {
    this.appStore.next({
      ...this.current,
      application: application,
      processing: false
    });
  }

  get isNew(): boolean {
    return this.current.application.id === undefined;
  }

  applicationCopyChange(application: Application) {
    this.appStore.next({
      ...this.current,
      applicationCopy: ObjectUtil.clone(application)
    });
  }

  currentOrCopy(): Application {
    const copy = this.snapshot.applicationCopy;
    if (copy) {
      this.appStore.next({
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

  changeDraft(draft: boolean) {
    this.appStore.next({...this.current, draft});
  }

  get tab(): Observable<SidebarItemType> {
    return this.appStore.pipe(
      map(state => state.tab),
      skip(1),
      distinctUntilChanged()
    );
  }

  changeTab(tab: SidebarItemType): void {
    this.appStore.next({...this.current, tab});
  }

  get attachments(): Observable<Array<AttachmentInfo>> {
    return this.appStore.pipe(
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
      tap(attachments => this.appStore.next({...this.current, attachments}))
    );
  }

  load(id: number): Observable<Application> {
    return this.applicationService.get(id).pipe(
      tap(application => {
        this.appStore.next({
          ...this.current,
          application,
          attachments: application.attachmentList,
          draft: application.statusEnum === ApplicationStatus.PRE_RESERVED
        });
        this.store.dispatch(new LoadSuccess(application.applicationTags));
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
    this.appStore.next({...this.current, processing: true});
    return this.applicationService.changeStatus(appId, status, changeInfo).pipe(
      tap(application => this.applicationChange(application)),
      catchError(err => {
        this.appStore.next({...this.current, processing: false});
        return observableThrowError(err);
      })
    );
  }

  changeRelatedProject(projectId: number) {
    this.appStore.next({...this.current, relatedProject: projectId});
  }

  loadDeposit(): Observable<Deposit> {
    const appId = this.snapshot.application.id;
    return this.depositService.fetchByApplication(appId).pipe(
      tap(deposit => this.appStore.next({...this.current, deposit}))
    );
  }

  get deposit(): Observable<Deposit> {
    return this.appStore.pipe(
      map(state => state.deposit),
      distinctUntilChanged()
    );
  }

  saveDeposit(deposit: Deposit): Observable<Deposit> {
    return this.depositService.save(deposit).pipe(
      tap(saved => {
        this.store.dispatch(new LoadTags());
        this.appStore.next({...this.current, deposit: saved});
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
    return this.store.select(fromApplication.getTags).pipe(
      take(1),
      switchMap(tags => {
        application.applicationTags = tags;
        if (this.snapshot.draft) {
          return this.applicationDraftService.save(application);
        } else {
          // Convert to full application
          if (application.statusEnum === ApplicationStatus.PRE_RESERVED) {
            return this.applicationDraftService.convertToApplication(application);
          } else {
            return this.applicationService.save(application);
          }
        }
      })
    );
  }

  private saved(application: Application): void {
    this.appStore.next({...this.current, application});
  }

  private get current(): ApplicationState {
    return this.appStore.getValue();
  }
}
