import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {MdDialog} from '@angular/material';

import {Application} from '../../model/application/application';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {findTranslation} from '../../util/translations';
import {NotificationService} from '../../service/notification/notification.service';
import {DECISION_MODAL_CONFIG, DecisionModalComponent} from './decision-modal.component';
import {DecisionConfirmation} from '../../model/decision/decision-confirmation';
import {Observable} from 'rxjs';
import {HttpResponse, HttpStatus} from '../../util/http-response';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DecisionDetails} from '../../model/decision/decision-details';
import {DECISION_PROPOSAL_MODAL_CONFIG, DecisionProposalModalComponent} from './proposal/decision-proposal-modal.component';
import {ApplicationState} from '../../service/application/application-state';
import {StatusChangeComment} from '../../model/application/status-change-comment';

@Component({
  selector: 'decision-actions',
  template: require('./decision-actions.component.html'),
  styles: [require('./decision-actions.component.scss')]
})
export class DecisionActionsComponent {
  @Input() application: Application;

  constructor(private applicationState: ApplicationState,
              private decisionHub: DecisionHub,
              private router: Router,
              private dialog: MdDialog) {}

  public decisionProposal(proposalType: string): void {
    let dialogRef = this.dialog.open(DecisionProposalModalComponent, DECISION_PROPOSAL_MODAL_CONFIG);
    let component = dialogRef.componentInstance;
    component.proposal = proposalType;
    dialogRef.afterClosed()
      .subscribe(proposal => this.proposalConfirmed(proposal));
  }

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

  private proposalConfirmed(comment: StatusChangeComment) {
    if (comment) {
      this.applicationState.changeStatus(new ApplicationStatusChange(this.application.id, ApplicationStatus.DECISIONMAKING, comment))
        .subscribe(app => {
          this.applicationState.loadComments(this.application.id).subscribe(); // Reload comments so they are updated in decision component
          NotificationService.message(findTranslation('application.statusChange.DECISIONMAKING'));
          this.application = app;
        }, err => NotificationService.errorMessage(findTranslation('application.error.toDecisionmaking')));
    }
  }

  private changeStatus(statusChange: ApplicationStatusChange): Observable<Application> {
    statusChange.id = this.application.id;
    return this.applicationState.changeStatus(statusChange)
      .do(application => this.statusChanged(application));
  }

  private statusChanged(application: Application): void {
    this.application = application;
    NotificationService.message(findTranslation(['decision.type', this.application.status, 'confirmation']));
  }

  private sendDecision(applicationId: number, decisionDetails: DecisionDetails): Observable<HttpResponse> {
    if (decisionDetails.hasEmails()) {
      return this.decisionHub.sendDecision(applicationId, decisionDetails);
    } else {
      return Observable.of(new HttpResponse(HttpStatus.OK));
    }
  }
}
