import {BehaviorSubject, combineLatest, forkJoin, Observable, of, Subject, throwError as observableThrowError} from 'rxjs';
import {Injectable} from '@angular/core';
import {Application} from '@model/application/application';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {AttachmentHub} from '@feature/application/attachment/attachment-hub';
import {SidebarItemType} from '@feature/sidebar/sidebar-item';
import {NumberUtil} from '@util/number.util';
import {ObjectUtil} from '@util/object.util';
import {ApplicationStatus} from '@model/application/application-status';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Deposit} from '@model/application/invoice/deposit';
import {DepositService} from './deposit/deposit.service';
import {ApplicationService} from './application.service';
import {isCommon} from '@model/application/attachment/attachment-type';
import {ApplicationDraftService} from './application-draft.service';
import {CustomerService} from '../customer/customer.service';
import {catchError, distinctUntilChanged, filter, map, switchMap, take, tap} from 'rxjs/internal/operators';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as TagAction from '@feature/application/actions/application-tag-actions';
import * as ApplicationAction from '@feature/application/actions/application-actions';
import {LoadSuccess} from '@feature/application/actions/application-actions';
import * as HistoryAction from '@feature/history/actions/history-actions';
import * as InvoicingCustomerAction from '@feature/application/invoicing/actions/invoicing-customer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationType} from '@model/application/type/application-type';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {Customer} from '@model/customer/customer';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {NotificationService} from '@feature/notification/notification.service';
import {findTranslation} from '@util/translations';
import {DefaultAttachmentInfo} from '@model/application/attachment/default-attachment-info';
import {Some} from '@util/option';
import {ClearCoordinates} from '@feature/map/actions/address-search-actions';

export interface ApplicationState {
  application?: Application;
  applicationCopy?: Application;
  attachments?: Array<AttachmentInfo>;
  tab?: SidebarItemType;
  relatedProject?: number;
  deposit?: Deposit;
  draft?: boolean;
  processing?: boolean;
  replacedDisableRemoveButton: boolean;
}

export const initialState: ApplicationState = {
  application: new Application(),
  applicationCopy: undefined,
  attachments: [],
  tab: 'BASIC_INFO',
  relatedProject: undefined,
  deposit: undefined,
  draft: false,
  processing: false,
  replacedDisableRemoveButton: false,
};

@Injectable()
export class ApplicationStore {
  private appStore = new BehaviorSubject<ApplicationState>(initialState);

  constructor(private applicationService: ApplicationService,
              private applicationDraftService: ApplicationDraftService,
              private customerService: CustomerService,
              private attachmentHub: AttachmentHub,
              private depositService: DepositService,
              private store: Store<fromRoot.State>,
              private notification: NotificationService) {
  }

  get snapshot(): ApplicationState {
    return ObjectUtil.clone(this.current);
  }

  get changes(): Observable<ApplicationState> {
    return this.appStore.asObservable().pipe(distinctUntilChanged());
  }

  reset(): void {
    this.appStore.next(initialState);
    this.store.dispatch(new ClearCoordinates());
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
      tap(replacement => {
        this.notification.translateSuccess('application.action.replaced');
        this.setAndDispatch(replacement);
      })
    );
  }

  changeDraft(draft: boolean) {
    this.appStore.next({...this.current, draft});
  }

  get tab(): Observable<SidebarItemType> {
    return this.appStore.pipe(
      map(state => state.tab),
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

  public setAndAction(app: Application): Action {
    this.setApplication(app);
    return new LoadSuccess(app);
  }

  public setAndDispatch(app: Application): void {
    this.store.dispatch(this.setAndAction(app));
  }

  setApplication(application: Application): void {
    this.appStore.next({
      ...this.current,
      application,
      processing: false,
      attachments: application.attachmentList,
      draft: application.targetState !== ApplicationStatus.PENDING && application.status === ApplicationStatus.PRE_RESERVED
    });
  }

  save(application: Application): Observable<Application> {
    return this.saveCustomersAndContacts(application).pipe(
      switchMap(app => this.saveApplication(app)),
      tap(app => this.saved(app)),
      take(1)
    );
  }

  saveInformationRequestResult(result: InformationRequestResult): Observable<Application> {
    const application = result.application;

    return this.saveCustomersAndContacts(application).pipe(
      switchMap(app => this.saveInvoicingCustomer(result, app)),
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
    return this.doChangeStatus(appId, status, changeInfo).pipe(
      tap(application => {
        this.setAndDispatch(application);
        this.store.dispatch(new HistoryAction.Load(ActionTargetType.Application));
        this.notification.translateSuccess(`application.statusChange.${status}`);

      }),
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
        this.store.dispatch(new TagAction.Load());
        this.appStore.next({...this.current, deposit: saved});
      })
    );
  }

  private saveInvoicingCustomer(result: InformationRequestResult, application: Application): Observable<Application> {
    return result.useCustomerForInvoicing === undefined
      ? this.saveAndUseInvoicingCustomer(result.invoiceCustomer, application)
      : this.useCustomerForInvoicing(result.useCustomerForInvoicing, application);
  }

  private useCustomerForInvoicing(roleType: CustomerRoleType, application: Application): Observable<Application> {
    const customer = application.customerWithContactsByRole(roleType);
    const updated = ObjectUtil.clone(application);
    updated.invoiceRecipientId = customer.customerId;
    return of(updated);
  }

  private saveAndUseInvoicingCustomer(customer: Customer, application: Application): Observable<Application> {
    if (customer) {
      return this.customerService.saveCustomer(customer).pipe(
        map(cust => {
          const updated = ObjectUtil.clone(application);
          updated.invoiceRecipientId = cust.id;
          return updated;
        })
      );
    } else {
      return of(application);
    }
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
    if (application.customersWithContacts.length) {
      const app = ObjectUtil.clone(application);
      return forkJoin(application.customersWithContacts.map(cwc =>
        this.customerService.saveCustomerWithContacts(cwc))
      ).pipe(
        map(savedCustomersWithContacts => {
          app.customersWithContacts = savedCustomersWithContacts;
          return app;
        })
      );
    } else {
      return of(application);
    }
  }

  private saveApplication(application: Application): Observable<Application> {
    return this.getApplicationForSave(application).pipe(
      switchMap(app => {
        if (!NumberUtil.isExisting(app) || app.status === ApplicationStatus.PRE_RESERVED) {
          return this.saveDraft(app);
        } else {
          return this.applicationService.save(app);
        }
      })
    );
  }

  private saveDraft(application: Application): Observable<Application> {
    const newApplication = !NumberUtil.isExisting(application);
    if (newApplication) {
      return this.applicationDraftService.save(application).pipe(
        tap(result => this.initDefaultAttachments(result))
      );
    } else if (this.snapshot.draft) {
      return this.applicationDraftService.save(application);
    } else {
      // Convert to full application
      return this.applicationDraftService.convertToApplication(application);
    }
  }

  private getApplicationForSave(application: Application): Observable<Application> {
    return combineLatest([
      this.store.select(fromApplication.getTags),
      this.store.select(fromApplication.getType),
      this.store.select(fromApplication.getKindsWithSpecifiers)
    ]).pipe(
      take(1),
      map(([tags, type, kindsWithSpecifiers]) => {
        const app = ObjectUtil.clone(application);
        app.applicationTags = tags;
        app.type = ApplicationType[type];
        app.kindsWithSpecifiers = kindsWithSpecifiers;
        return app;
      })
    );
  }

  private saved(application: Application): void {
    this.appStore.next({...this.current, application});
    this.store.dispatch(new ApplicationAction.LoadSuccess(application));
    this.store.dispatch(new HistoryAction.Load(ActionTargetType.Application));
    this.store.dispatch(new InvoicingCustomerAction.Load());
  }

  private get current(): ApplicationState {
    return this.appStore.getValue();
  }

  private doChangeStatus(appId: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    if (status === ApplicationStatus.RETURNED_TO_PREPARATION) {
      return this.applicationService.returnToEditing(appId, changeInfo);
    } else {
      return this.applicationService.changeStatus(appId, status, changeInfo);
    }
  }

  private initDefaultAttachments(application: Application): void {
    this.defaultAttachmentsForArea(application).pipe(
      take(1),
      switchMap(attachments => this.saveAttachments(application.id, attachments))
    ).subscribe(
      () => {},
      err => this.notification.error(findTranslation('attachment.error.defaultAttachmentByArea')));
  }

  private defaultAttachmentsForArea(application: Application): Observable<Array<DefaultAttachmentInfo>> {
    return Some(application.firstLocation)
      .map(loc => loc.fixedLocationIds)
      .map(ids => this.attachmentHub.defaultAttachmentInfosByArea(application.type, ids))
      .orElse(of([]));
  }
}
