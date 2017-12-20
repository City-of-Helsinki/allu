import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ApplicationStore} from '../../../service/application/application-store';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationType} from '../../../model/application/type/application-type';
import {NotificationService} from '../../../service/notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../../model/application/application';
import {Some} from '../../../util/option';
import {NumberUtil} from '../../../util/number.util';
import {Subscription} from 'rxjs/Subscription';
import {MODIFY_ROLES, RoleType} from '../../../model/user/role-type';
import {ObjectUtil} from '../../../util/object.util';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material';
import {findTranslation} from '../../../util/translations';

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
  @Input() form: FormGroup;
  @Input() status: string;
  @Input() submitPending: boolean;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  showDecision = true;
  decisionDisabled = false;
  showHandling = true;
  showEdit = true;
  showDelete = false;
  showCancel = false;
  showReplace = false;
  applicationId: number;

  private applicationSub: Subscription;

  constructor(private router: Router,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.applicationSub = this.applicationStore.application.subscribe(app => {
      const status = app.statusEnum;
      this.showDecision = this.showDecisionForApplication(app);
      this.decisionDisabled = !this.validForDecision(app);
      this.showHandling = status < ApplicationStatus.HANDLING;
      this.showDelete = app.typeEnum === ApplicationType.NOTE;
      this.showCancel = status < ApplicationStatus.DECISION;
      this.showEdit = status < ApplicationStatus.DECISION;
      this.showReplace = status === ApplicationStatus.DECISION;
      this.applicationId = app.id;
    });
  }

  ngOnDestroy(): void {
    this.applicationSub.unsubscribe();
  }

  copyApplicationAsNew(): void {
    const application = ObjectUtil.clone(this.applicationStore.snapshot.application);
    application.id = undefined;
    application.attachmentList = [];
    application.locations = application.locations.map(loc => loc.copyAsNew());
    this.applicationStore.applicationCopyChange(application);
    this.router.navigate(['/applications/edit']);
  }

  replace(): void {
    this.applicationStore.replace()
      .subscribe(
        (application) => {
          NotificationService.translateMessage('application.action.replaced');
          this.router.navigate(['/applications', application.id, 'summary']);
        },
        (error) => NotificationService.translateMessage(error));
  }

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.HANDLING)
      .subscribe(app => {
        NotificationService.translateMessage('application.statusChange.HANDLING');
        this.applicationStore.applicationChange(app);
        this.router.navigate(['/applications', this.applicationStore.snapshot.application.id, 'edit']);
    },
    err => NotificationService.translateErrorMessage('application.error.toHandling'));
  }

  toDecisionmaking(): void {
    this.moveToDecisionMaking().subscribe(app => this.router.navigate(['/applications', app.id, 'decision']));
  }

  delete(): void {
    Some(this.applicationStore.snapshot.application.id).do(id => this.applicationStore.delete(id).subscribe(
      response => {
        NotificationService.translateMessage('application.action.deleted');
        this.router.navigate(['/']);
      },
      error => NotificationService.error(error)));
  }

  cancel(): void {
    if (this.applicationStore.snapshot.application.statusEnum < ApplicationStatus.DECISION) {
      const data = {
        title: findTranslation('application.confirmCancel.title'),
        confirmText: findTranslation('application.confirmCancel.confirmText'),
        cancelText: findTranslation('application.confirmCancel.cancelText')
      };

      this.dialog.open(ConfirmDialogComponent, { data })
        .afterClosed()
        .filter(result => result) // Ignore no answers
        .subscribe(() => this.cancelApplication());
    }
  }

  private cancelApplication(): void {
    this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.CANCELLED)
      .subscribe(app => {
          NotificationService.translateMessage('application.statusChange.CANCELLED');
          this.applicationStore.applicationChange(app);
          this.router.navigate(['/workqueue']);
        },
        err => NotificationService.translateErrorMessage('application.error.cancel'));
  }

  private moveToDecisionMaking(): Observable<Application> {
    if (this.shouldMoveToDecisionMaking()) {
      return this.applicationStore.changeStatus(this.applicationStore.snapshot.application.id, ApplicationStatus.DECISIONMAKING)
        .map(app => {
          NotificationService.translateMessage('application.statusChange.DECISIONMAKING');
          this.applicationStore.applicationChange(app);
          return app;
        },
        err => NotificationService.translateErrorMessage('application.error.toDecisionmaking'));
    } else {
      return Observable.of(this.applicationStore.snapshot.application);
    }
  }

  private shouldMoveToDecisionMaking(): boolean {
    const app = this.applicationStore.snapshot.application;
    const appType = app.typeEnum;
    const status =  app.statusEnum;
    return appType === ApplicationType.CABLE_REPORT && status === ApplicationStatus.HANDLING;
  }

  private showDecisionForApplication(app: Application): boolean {
    const validType = ApplicationType[app.type] !== ApplicationType.NOTE;
    const validStatus = ApplicationStatus[app.status] >= ApplicationStatus.HANDLING;
    return validType && validStatus;
  }

  private validForDecision(app: Application): boolean {
    return NumberUtil.isDefined(app.invoiceRecipientId) || app.notBillable;
  }
}
