import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {MdDialog} from '@angular/material';

import {Application} from '../../model/application/application';
import {ApplicationHub} from '../../service/application/application-hub';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {findTranslation} from '../../util/translations';
import {NotificationService} from '../../service/notification/notification.service';
import {DecisionModalComponent, DECISION_MODAL_CONFIG} from './decision-modal.component';
import {DecisionConfirmation} from '../../model/decision/decision-confirmation';
import {Observable} from 'rxjs';
import {HttpResponse} from '../../util/http-response';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DecisionDetails} from '../../model/decision/decision-details';

@Component({
  selector: 'decision-actions',
  template: require('./decision-actions.component.html'),
  styles: [require('./decision-actions.component.scss')]
})
export class DecisionActionsComponent {
  @Input() application: Application;

  constructor(private applicationHub: ApplicationHub,
              private decisionHub: DecisionHub,
              private router: Router,
              private dialog: MdDialog) {}

  public decision(status: string): void {
    let dialogRef = this.dialog.open(DecisionModalComponent, DECISION_MODAL_CONFIG);
    let component = dialogRef.componentInstance;
    component.applicationId = this.application.id;
    component.status = status;
    component.distributionList = this.application.decisionDistributionList;
    dialogRef.afterClosed()
      .subscribe((result: DecisionConfirmation) => this.decisionConfirmed(result));
  }

  public decisionConfirmed(confirm: DecisionConfirmation) {
    if (!!confirm) {
      this.changeStatus(confirm.statusChange)
        .switchMap(app => this.sendDecision(app.id, confirm.decisionDetails))
        .subscribe(
          result => this.router.navigateByUrl('/workqueue'),
          error => NotificationService.error(error));
    }
  }

  private changeStatus(statusChange: ApplicationStatusChange): Observable<Application> {
    statusChange.id = this.application.id;
    return this.applicationHub.changeStatus(statusChange)
      .do(application => this.statusChanged(application));
  }

  private statusChanged(application: Application): void {
    this.application = application;
    NotificationService.message(findTranslation(['decision.type', this.application.status, 'confirmation']));
  }

  private sendDecision(applicationId: number, decisionDetails: DecisionDetails): Observable<HttpResponse> {
    return this.decisionHub.sendDecision(applicationId, decisionDetails);
  }
}
