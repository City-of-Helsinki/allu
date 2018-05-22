import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationStatus, inHandling} from '../../model/application/application-status';
import {findTranslation} from '../../util/translations';
import {NotificationService} from '../../service/notification/notification.service';
import {DECISION_MODAL_CONFIG, DecisionConfirmation, DecisionModalComponent} from './decision-modal.component';
import {DecisionHub} from '../../service/decision/decision-hub';
import {DECISION_PROPOSAL_MODAL_CONFIG, DecisionProposalModalComponent} from './proposal/decision-proposal-modal.component';
import {ApplicationStore} from '../../service/application/application-store';
import {StatusChangeInfo} from '../../model/application/status-change-info';
import {Some} from '../../util/option';
import {DecisionDetails} from '../../model/decision/decision-details';
import * as fromApplication from '../application/reducers';
import {Store} from '@ngrx/store';
import {Load} from '../comment/actions/comment-actions';
import {ActionTargetType} from '../allu/actions/action-target-type';


@Component({
  selector: 'decision-actions',
  templateUrl: './decision-actions.component.html',
  styleUrls: ['./decision-actions.component.scss']
})
export class DecisionActionsComponent implements OnInit, OnChanges {
  @Input() application: Application;
  @Output() onDecisionConfirm = new EventEmitter<StatusChangeInfo>();

  showProposal = false;
  showDecision = false;

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromApplication.State>,
              private decisionHub: DecisionHub,
              private router: Router,
              private dialog: MatDialog,
              private notification: NotificationService) {}

  ngOnInit(): void {
  }


  ngOnChanges(changes: SimpleChanges): void {
    const status = this.application.statusEnum;
    this.showProposal = inHandling(status);
    this.showDecision = ApplicationStatus.DECISIONMAKING === status;
  }

  public decisionProposal(proposalType: string): void {
    const dialogRef = this.dialog.open<DecisionProposalModalComponent>(DecisionProposalModalComponent, DECISION_PROPOSAL_MODAL_CONFIG);
    const component = dialogRef.componentInstance;
    component.proposal = proposalType;
    dialogRef.afterClosed()
      .subscribe(proposal => this.proposalConfirmed(proposal));
  }

  public decision(status: string): void {
    const config = {...DECISION_MODAL_CONFIG};
    config.data.status = ApplicationStatus[status];
    config.data.distributionList = this.application.decisionDistributionList;

    const dialogRef = this.dialog.open<DecisionModalComponent>(DecisionModalComponent, config);
    dialogRef.afterClosed()
      .subscribe((result: DecisionConfirmation) => this.decisionConfirmed(result));
  }

  public decisionConfirmed(confirmation: DecisionConfirmation) {
    if (!!confirmation) {
      this.changeStatus(confirmation)
        .switchMap(app => this.sendDecision(app.id, confirmation))
        .subscribe(
          () => this.router.navigateByUrl('/workqueue'),
          error => this.notification.errorInfo(error));
    }
  }

  private proposalConfirmed(changeInfo: StatusChangeInfo) {
    if (changeInfo) {
      this.applicationStore.changeStatus(this.application.id, ApplicationStatus.DECISIONMAKING, changeInfo)
        .subscribe(app => {
          this.store.dispatch(new Load(ActionTargetType.Application));
          this.notification.success(findTranslation('application.statusChange.DECISIONMAKING'));
          this.applicationStore.applicationChange(app);
          this.onDecisionConfirm.emit(changeInfo);
        }, err => this.notification.error(findTranslation('application.error.toDecisionmaking')));
    }
  }

  private changeStatus(confirmation: DecisionConfirmation): Observable<Application> {
    const changeInfo = new StatusChangeInfo(undefined, confirmation.comment, confirmation.owner);
    return this.applicationStore.changeStatus(this.application.id, confirmation.status, changeInfo)
      .do(application => this.statusChanged(application));
  }

  private statusChanged(application: Application): void {
    this.application = application;
    this.notification.success(findTranslation(['decision.type', this.application.status, 'confirmation']));
  }

  private sendDecision(appId: number, confirmation: DecisionConfirmation): Observable<{}> {
    return Some(confirmation.distributionList)
      .filter(distribution => distribution.length > 0)
      .map(distribution => new DecisionDetails(distribution, confirmation.emailMessage))
      .map(details => this.decisionHub.sendDecision(appId, details))
      .orElseGet(() => Observable.of({}));
  }
}
