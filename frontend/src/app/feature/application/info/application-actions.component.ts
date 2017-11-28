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
  @Input() applicationId: number;
  @Input() form: FormGroup;
  @Input() status: string;
  @Input() submitPending: boolean;

  showDecision = true;
  decisionDisabled = false;
  showHandling = true;
  showDelete = false;
  showCancel = false;

  private applicationSub: Subscription;

  constructor(private router: Router, private applicationStore: ApplicationStore) {
  }

  ngOnInit(): void {
    this.applicationSub = this.applicationStore.changes.subscribe(app => {
      const status = app.statusEnum;
      this.showDecision = this.showDecisionForApplication(app);
      this.decisionDisabled = !this.validForDecision(app);
      this.showHandling = status < ApplicationStatus.HANDLING;
      this.showDelete = app.typeEnum === ApplicationType.NOTE;
      this.showCancel = status < ApplicationStatus.DECISION;
    });
  }

  ngOnDestroy(): void {
    this.applicationSub.unsubscribe();
  }

  copyApplicationAsNew(): void {
    const application = this.applicationStore.application;
    application.id = undefined;
    application.attachmentList = [];
    application.locations = application.locations.map(loc => loc.copyAsNew());
    this.applicationStore.applicationCopy = application;
    this.router.navigate(['/applications/edit']);
  }

  moveToHandling(): void {
    this.applicationStore.changeStatus(this.applicationId, ApplicationStatus.HANDLING)
      .subscribe(app => {
        NotificationService.translateMessage('application.statusChange.HANDLING');
        this.applicationStore.application = app;
        this.router.navigate(['/applications', this.applicationId, 'edit']);
    },
    err => NotificationService.translateErrorMessage('application.error.toHandling'));
  }

  toDecisionmaking(): void {
    this.moveToDecisionMaking().subscribe(app => this.router.navigate(['/applications', app.id, 'decision']));
  }

  delete(): void {
    Some(this.applicationId).do(id => this.applicationStore.delete(id).subscribe(
      response => {
        NotificationService.translateMessage('application.action.deleted');
        this.router.navigate(['/']);
      },
      error => NotificationService.error(error)));
  }

  cancel(): void {
    if (this.applicationStore.application.statusEnum < ApplicationStatus.DECISION) {
      this.applicationStore.changeStatus(this.applicationId, ApplicationStatus.CANCELLED)
        .subscribe(app => {
            NotificationService.translateMessage('application.statusChange.CANCELLED');
            this.applicationStore.application = app;
            this.router.navigate(['/workqueue']);
          },
          err => NotificationService.translateErrorMessage('application.error.cancel'));
    }
  }

  private moveToDecisionMaking(): Observable<Application> {
    if (this.shouldMoveToDecisionMaking()) {
      return this.applicationStore.changeStatus(this.applicationId, ApplicationStatus.DECISIONMAKING)
        .map(app => {
          NotificationService.translateMessage('application.statusChange.DECISIONMAKING');
          this.applicationStore.application = app;
          return app;
        },
        err => NotificationService.translateErrorMessage('application.error.toDecisionmaking'));
    } else {
      return Observable.of(this.applicationStore.application);
    }
  }

  private shouldMoveToDecisionMaking(): boolean {
    const appType = this.applicationStore.application.typeEnum;
    const status =  this.applicationStore.application.statusEnum;
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
