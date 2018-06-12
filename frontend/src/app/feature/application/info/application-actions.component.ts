import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ApplicationStore} from '../../../service/application/application-store';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {NotificationService} from '../../../service/notification/notification.service';
import {Observable, of, Subscription} from 'rxjs';
import {Application} from '../../../model/application/application';
import {Some} from '../../../util/option';
import {NumberUtil} from '../../../util/number.util';
import {MODIFY_ROLES, RoleType} from '../../../model/user/role-type';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material';
import {findTranslation} from '../../../util/translations';
import {User} from '../../../model/user/user';
import {UserSearchCriteria} from '../../../model/user/user-search-criteria';
import {ArrayUtil} from '../../../util/array-util';
import {UserHub} from '../../../service/user/user-hub';
import {catchError, filter, map} from 'rxjs/internal/operators';

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

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  showDecision = true;
  decisionDisabled = false;
  showHandling = true;
  showEdit = true;
  showDelete = false;
  showCancel = false;
  showReplace = false;
  showConvertToApplication = false;
  applicationId: number;

  private applicationSub: Subscription;

  constructor(private router: Router,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog,
              private userHub: UserHub,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.applicationSub = this.applicationStore.application.subscribe(app => {
      const status = app.statusEnum;
      this.showDecision = this.showDecisionForApplication(app);
      this.decisionDisabled = !this.validForDecision(app);
      this.showHandling = (status === ApplicationStatus.PENDING) && (app.typeEnum !== ApplicationType.NOTE);
      this.showDelete = (app.typeEnum === ApplicationType.NOTE) || (status === ApplicationStatus.PRE_RESERVED);
      this.showCancel = status <= ApplicationStatus.DECISION;
      this.showEdit = this.readonly && (status < ApplicationStatus.DECISION);
      this.showReplace = status === ApplicationStatus.DECISION;
      this.showConvertToApplication = status === ApplicationStatus.PRE_RESERVED;
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
    application.statusEnum = application.statusEnum === ApplicationStatus.PRE_RESERVED
      ? ApplicationStatus.PRE_RESERVED
      : ApplicationStatus.PENDING;
    application.attachmentList = [];
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
      app => this.router.navigate(['/applications', app.id, 'decision']),
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
    if (this.applicationStore.snapshot.application.statusEnum <= ApplicationStatus.DECISION) {
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
    const appType = app.typeEnum;
    const status = app.statusEnum;
    return (appType === ApplicationType.CABLE_REPORT ||
            appType === ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS) &&
            status === ApplicationStatus.HANDLING;
  }

  private showDecisionForApplication(app: Application): boolean {
    const validType = ApplicationType[app.type] !== ApplicationType.NOTE;
    const validStatus = ApplicationStatus[app.status] >= ApplicationStatus.HANDLING;
    return validType && validStatus;
  }

  private validForDecision(app: Application): boolean {
    return NumberUtil.isDefined(app.invoiceRecipientId) || app.notBillable;
  }

  private findDefaultRegionalOwner(app: Application): Observable<User> {
    const criteria = new UserSearchCriteria(RoleType.ROLE_PROCESS_APPLICATION, app.typeEnum, app.firstLocation.effectiveCityDistrictId);
    return this.userHub.searchUsers(criteria).pipe(
      map(preferred => ArrayUtil.first(preferred))
    );
  }
}
