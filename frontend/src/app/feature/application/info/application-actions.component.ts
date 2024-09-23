import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';
import {applicationCanBeEdited, ApplicationStatus, isSameOrAfter, isSameOrBefore} from '@model/application/application-status';
import {ApplicationType} from '@model/application/type/application-type';
import {NotificationService} from '@feature/notification/notification.service';
import {Observable, Subject, Subscription} from 'rxjs';
import {Application} from '@model/application/application';
import {Some} from '@util/option';
import {NumberUtil} from '@util/number.util';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {findTranslation} from '@util/translations';
import {User} from '@model/user/user';
import {UserSearchCriteria} from '@model/user/user-search-criteria';
import {ArrayUtil} from '@util/array-util';
import {filter, map, take, takeUntil} from 'rxjs/operators';
import {InformationRequest} from '@model/information-request/information-request';
import {ApplicationUtil} from '@feature/application/application-util';
import {UserService} from '@service/user/user-service';
import {
  ApplicationExtension,
  isCustomerStartEndTimes,
  isGuaranteeEndTime,
  isOperationalConditionDates,
  isWorkFinishedDates,
  OperationalConditionDates,
  WorkFinishedDates
} from '@app/model/application/type/application-extension';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {CancelRequest} from '@feature/information-request/actions/information-request-actions';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {TerminationModalService} from '@feature/decision/termination/termination-modal-service';
import { selectRemoveButtonDisabled } from '../reducers';

@Component({
  selector: 'application-actions',
  viewProviders: [],
  templateUrl: './application-actions.component.html',
  styleUrls: [
    './application-actions.component.scss'
  ]
})
export class ApplicationActionsComponent implements OnInit, OnDestroy {

  @Input() readonly = true;
  @Input() status: string;
  @Input() submitPending: boolean;
  @Input() valid: boolean;
  @Input() pendingClientData: boolean;
  @Input() pendingInformationRequestResponse: boolean;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  showDecision = true;
  decisionDisabled = false;
  showHandling = true;
  showEdit = true;
  showDelete = false;
  showCancel = false;
  showReplace = false;
  showConvertToApplication = false;
  showInformationRequest = false;
  showCancelInformationRequest = false;
  showTermination = false;
  showActions = true;
  applicationId: number;
  type: ApplicationType;
  removeButtonDisabled$: Observable<boolean>;
  private destroy$ = new Subject<void>();

  private _informationRequest: InformationRequest;
  private applicationSub: Subscription;

  constructor(private router: Router,
              private store: Store<fromRoot.State>,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog,
              private userService: UserService,
              private notification: NotificationService,
              private terminationModalService: TerminationModalService,
              private route: ActivatedRoute) {

    this.removeButtonDisabled$ = this.store.pipe(select(selectRemoveButtonDisabled));
  }

  
  ngOnInit(): void {

    this.applicationSub = this.applicationStore.application.subscribe(app => {
      const status = app.status;
      this.showDecision = this.showDecisionForApplication(app);
      this.decisionDisabled = !this.validForDecision(app);
      this.showHandling = (status === ApplicationStatus.PENDING) && (app.type !== ApplicationType.NOTE);
      this.showDelete = (app.type === ApplicationType.NOTE) || (status === ApplicationStatus.PRE_RESERVED);
      this.showCancel = isSameOrBefore(status, ApplicationStatus.DECISION);
      this.showEdit = this.readonly && applicationCanBeEdited(app);
      this.showReplace = ArrayUtil.contains([ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION], status)
        && !NumberUtil.isDefined(app.replacedByApplicationId);
      this.showConvertToApplication = status === ApplicationStatus.PRE_RESERVED;
      this.showActions = (status !== ApplicationStatus.PENDING_CLIENT) && (status !== ApplicationStatus.WAITING_CONTRACT_APPROVAL);
      this.showInformationRequest = ApplicationUtil.validForInformationRequest(app);
      this.showTermination =
        (app.type === ApplicationType.SHORT_TERM_RENTAL && status === ApplicationStatus.DECISION) ||
        (app.type === ApplicationType.PLACEMENT_CONTRACT && status === ApplicationStatus.DECISION) ||
        (app.type === ApplicationType.PLACEMENT_CONTRACT && status === ApplicationStatus.FINISHED);
      this.applicationId = app.id;
      this.type = app.type;
    });

    // ALLU-17, if application has been replaced and the replacement is in any other than CANCEL state, hide cancel button.
    this.removeButtonDisabled$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(
    (disabled => {
      if (disabled) this.showCancel = false;
    }));
  }

  ngOnDestroy(): void {
    this.applicationSub.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }

  @Input() set informationRequest(informationRequest: InformationRequest) {
    this._informationRequest = informationRequest;

    this.showCancelInformationRequest = Some(this.informationRequest)
      .map(ir => ArrayUtil.contains([InformationRequestStatus.DRAFT, InformationRequestStatus.OPEN], ir.status))
      .orElse(false);
  }

  get informationRequest() {
    return this._informationRequest;
  }

  copyApplicationAsNew(): void {
    const application = this.applicationStore.snapshot.application;
    application.id = undefined;
    application.applicationId = undefined;
    application.handler = undefined;
    application.decisionMaker = undefined;
    application.decisionTime = undefined;
    application.invoicingDate = undefined;
    application.invoiceRecipientId = undefined;
    application.skipPriceCalculation = false;
    application.customerReference = undefined;
    application.replacesApplicationId = undefined;
    application.replacedByApplicationId = undefined;
    application.identificationNumber = undefined;
    // Pre-reserved should be kept as such
    application.status = application.status === ApplicationStatus.PRE_RESERVED
      ? ApplicationStatus.PRE_RESERVED
      : ApplicationStatus.PENDING;
    application.attachmentList = [];
    application.applicationTags = [];
    application.locations = application.locations.map(loc => loc.copyAsNew());
    application.project = undefined;
    application.extension = this.copyExtension(application.extension);

    this.findDefaultRegionalOwner(application).subscribe(owner => {
      application.owner = owner;
      this.applicationStore.applicationCopyChange(application);
      this.router.navigate(['/applications/edit']);
    });
  }

  private copyExtension(extension: ApplicationExtension): ApplicationExtension {
    if (isWorkFinishedDates(extension)) {
      this.clearWorkFinishedDates(extension);
    }

    if (isOperationalConditionDates(extension)) {
      this.clearOperationalConditionDates(extension);
    }

    if (isGuaranteeEndTime(extension)) {
      extension.guaranteeEndTime = undefined;
    }

    if (isCustomerStartEndTimes(extension)) {
      extension.customerStartTime = undefined;
      extension.customerEndTime = undefined;
    }

    return extension;
  }

  private clearWorkFinishedDates(extension: WorkFinishedDates): void {
    extension.workFinished = undefined;
    extension.customerWorkFinished = undefined;
    extension.workFinishedReported = undefined;
  }

  private clearOperationalConditionDates(extension: OperationalConditionDates): void {
    extension.winterTimeOperation = undefined;
    extension.customerWinterTimeOperation = undefined;
    extension.operationalConditionReported = undefined;
  }

  replace(): void {
    this.applicationStore.replace()
      .subscribe(
        (application) => this.router.navigate(['/applications', application.id, 'summary']),
        (error) => this.notification.translateSuccess(error));
  }

  convertToApplication(): void {
    this.applicationStore.changeDraft(false);
    this.router.navigate(['/applications', this.applicationStore.snapshot.application.id, 'edit']);
  }

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.HANDLING)
      .subscribe(app => this.router.navigate(['/applications', app.id, 'edit']),
        err => this.notification.translateErrorMessage('application.error.toHandling'));
  }

  toDecisionmaking(): void {
    this.router.navigate(['/applications', this.applicationStore.snapshot.application.id, 'summary', 'decision']);
  }

  delete(): void {
    Some(this.applicationStore.snapshot.application.id).do(id => this.applicationStore.delete(id).subscribe(
      response => {
        this.notification.translateSuccess('application.action.deleted');
        this.router.navigate(['/']);
      },
      error => this.notification.errorInfo(error)));
  }

  cancel(): void {
    if (isSameOrBefore(this.applicationStore.snapshot.application.status, ApplicationStatus.DECISION)) {
      const data = {
        title: findTranslation('application.confirmCancel.title'),
        confirmText: findTranslation('application.confirmCancel.confirmText'),
        cancelText: findTranslation('application.confirmCancel.cancelText')
      };

      this.dialog.open(ConfirmDialogComponent, {data}).afterClosed().pipe(
        filter(result => !!result) // Ignore no answers
      ).subscribe(() => this.cancelApplication());
    }
  }

  showExternalUpdates(): void {
    this.router.navigate(['pending_info'], {relativeTo: this.route});
  }

  showInformationRequestInfo(): void {
    this.router.navigate(['information_request'], {relativeTo: this.route});
  }

  cancelInformationRequest(): void {
    Some(this.informationRequest)
      .map(request => request.informationRequestId)
      .do(id => this.store.dispatch(new CancelRequest(id)));
  }

  private cancelApplication(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.CANCELLED)
      .subscribe(
        () => this.router.navigate(['/workqueue']),
        err => this.notification.translateErrorMessage('application.error.cancel'));
  }

  private showDecisionForApplication(app: Application): boolean {
    const validType = app.type !== ApplicationType.NOTE;
    const validStatus = isSameOrAfter(app.status, ApplicationStatus.HANDLING);
    return validType && validStatus;
  }

  private validForDecision(app: Application): boolean {
    return NumberUtil.isDefined(app.invoiceRecipientId) || app.notBillable;
  }

  private findDefaultRegionalOwner(app: Application): Observable<User> {
    const criteria = new UserSearchCriteria(RoleType.ROLE_PROCESS_APPLICATION, app.type, app.firstLocation.effectiveCityDistrictId);
    return this.userService.search(criteria).pipe(
      map(preferred => preferred.filter(user => !user.hasRole(RoleType.ROLE_SUPERVISE))),
      map(preferred => ArrayUtil.first(preferred))
    );
  }

  showTerminationModal(): void {
    this.terminationModalService.showTerminationModal();
  }
}
