import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';
import {applicationCanBeEdited, ApplicationStatus, isSameOrAfter, isSameOrBefore} from '@model/application/application-status';
import {ApplicationType, automaticDecisionmaking} from '@model/application/type/application-type';
import {NotificationService} from '@feature/notification/notification.service';
import {Observable, of, Subscription} from 'rxjs';
import {Application} from '@model/application/application';
import {Some} from '@util/option';
import {NumberUtil} from '@util/number.util';
import {MODIFY_ROLES, RoleType} from '@model/user/role-type';
import {ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material';
import {findTranslation} from '@util/translations';
import {User} from '@model/user/user';
import {UserSearchCriteria} from '@model/user/user-search-criteria';
import {ArrayUtil} from '@util/array-util';
import {filter, map} from 'rxjs/internal/operators';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';
import {InformationRequest} from '@model/information-request/information-request';
import {ApplicationUtil} from '@feature/application/application-util';
import {UserService} from '@service/user/user-service';

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
  @Input() informationRequest: InformationRequest;

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
  showActions = true;
  applicationId: number;

  private applicationSub: Subscription;

  constructor(private router: Router,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog,
              private userService: UserService,
              private notification: NotificationService,
              private modalState: InformationRequestModalEvents) {
  }

  ngOnInit(): void {
    this.applicationSub = this.applicationStore.application.subscribe(app => {
      const status = app.status;
      this.showDecision = this.showDecisionForApplication(app);
      this.decisionDisabled = !this.validForDecision(app);
      this.showHandling = (status === ApplicationStatus.PENDING) && (app.type !== ApplicationType.NOTE);
      this.showDelete = (app.type === ApplicationType.NOTE) || (status === ApplicationStatus.PRE_RESERVED);
      this.showCancel = isSameOrBefore(status, ApplicationStatus.DECISION);
      this.showEdit = this.readonly && applicationCanBeEdited(status);
      this.showReplace = status === ApplicationStatus.DECISION;
      this.showConvertToApplication = status === ApplicationStatus.PRE_RESERVED;
      this.showActions = (status !== ApplicationStatus.PENDING_CLIENT) && (status !== ApplicationStatus.WAITING_CONTRACT_APPROVAL);
      this.showInformationRequest = ApplicationUtil.validForInformationRequest(app);
      this.applicationId = app.id;
    });
  }

  ngOnDestroy(): void {
    this.applicationSub.unsubscribe();
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
    // Pre-reserved should be kept as such
    application.status = application.status === ApplicationStatus.PRE_RESERVED
      ? ApplicationStatus.PRE_RESERVED
      : ApplicationStatus.PENDING;
    application.attachmentList = [];
    application.applicationTags = [];
    application.locations = application.locations.map(loc => loc.copyAsNew());
    application.project = undefined;
    this.findDefaultRegionalOwner(application).subscribe(owner => {
      application.owner = owner;
      this.applicationStore.applicationCopyChange(application);
      this.router.navigate(['/applications/edit']);
    });
  }

  replace(): void {
    this.applicationStore.replace()
      .subscribe(
        (application) => {
          this.notification.translateSuccess('application.action.replaced');
          this.router.navigate(['/applications', application.id, 'summary']);
        },
        (error) => this.notification.translateSuccess(error));
  }

  convertToApplication(): void {
    this.applicationStore.changeDraft(false);
    this.router.navigate(['/applications', this.applicationStore.snapshot.application.id, 'edit']);
  }

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.HANDLING)
      .subscribe(app => {
          this.notification.translateSuccess('application.statusChange.HANDLING');
          this.applicationStore.applicationChange(app);
          this.router.navigate(['/applications', this.applicationStore.snapshot.application.id, 'edit']);
        },
        err => this.notification.translateErrorMessage('application.error.toHandling'));
  }

  toDecisionmaking(): void {
    this.moveToDecisionMaking().subscribe(
      app => this.router.navigate(['/applications', app.id, 'summary', 'decision']),
      err => this.notification.translateErrorMessage('application.error.toDecisionmaking')
    );
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
    this.modalState.openAcceptance();
  }

  showInformationRequestInfo(): void {
    this.modalState.openRequest();
  }

  private cancelApplication(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.CANCELLED)
      .subscribe(app => {
          this.notification.translateSuccess('application.statusChange.CANCELLED');
          this.applicationStore.applicationChange(app);
          this.router.navigate(['/workqueue']);
        },
        err => this.notification.translateErrorMessage('application.error.cancel'));
  }

  private moveToDecisionMaking(): Observable<Application> {
    if (this.shouldMoveToDecisionMaking()) {
      return this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.DECISIONMAKING).pipe(
        map(app => {
          this.notification.translateSuccess('application.statusChange.DECISIONMAKING');
          this.applicationStore.applicationChange(app);
          return app;
        })
      );
    } else {
      return of(this.applicationStore.snapshot.application);
    }
  }

  private shouldMoveToDecisionMaking(): boolean {
    const app = this.applicationStore.snapshot.application;
    return (automaticDecisionmaking.indexOf(app.type) >= 0) && app.status === ApplicationStatus.HANDLING;
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
      map(preferred => ArrayUtil.first(preferred))
    );
  }
}
