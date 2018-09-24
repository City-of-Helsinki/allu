import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import {Observable, of} from 'rxjs';

import {Application} from '../../model/application/application';
import {ApplicationStatus, inHandling} from '../../model/application/application-status';
import {findTranslation} from '../../util/translations';
import {NotificationService} from '../notification/notification.service';
import {DECISION_MODAL_CONFIG, DecisionConfirmation, DecisionModalComponent} from './decision-modal.component';
import {DecisionService} from '../../service/decision/decision.service';
import {DECISION_PROPOSAL_MODAL_CONFIG, DecisionProposalModalComponent} from './proposal/decision-proposal-modal.component';
import {ApplicationStore} from '../../service/application/application-store';
import {StatusChangeInfo} from '../../model/application/status-change-info';
import {Some} from '../../util/option';
import {DecisionDetails} from '../../model/decision/decision-details';
import * as fromApplication from '../application/reducers';
import {Store} from '@ngrx/store';
import {Load} from '../comment/actions/comment-actions';
import * as tagActions from '../application/actions/application-tag-actions';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {filter, switchMap, tap, catchError} from 'rxjs/internal/operators';
import {CommentType} from '@model/application/comment/comment-type';

const RESEND_ALLOWED = [
  ApplicationStatus.DECISION,
  ApplicationStatus.FINISHED,
  ApplicationStatus.ARCHIVED,
];

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
  showResend = false;

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromApplication.State>,
              private decisionService: DecisionService,
              private router: Router,
              private dialog: MatDialog,
              private notification: NotificationService) {}

  ngOnInit(): void {
  }


  ngOnChanges(changes: SimpleChanges): void {
    const status = this.application.status;
    this.showProposal = inHandling(status);
    this.showDecision = ApplicationStatus.DECISIONMAKING === status;
    this.showResend = RESEND_ALLOWED.indexOf(status) >= 0;
  }

  public decisionProposal(proposalType: string): void {
    const config = {
      ...DECISION_PROPOSAL_MODAL_CONFIG,
      data: {
        proposalType,
        cityDistrict: this.application.firstLocation.effectiveCityDistrictId
      }
    };

    this.dialog.open<DecisionProposalModalComponent>(DecisionProposalModalComponent, config).afterClosed()
      .subscribe(proposal => this.proposalConfirmed(proposal));
  }

  public decision(status: string): void {
    this.confirmDecisionSend(status, status)
      .subscribe(result => this.decisionConfirmed(result));
  }

  public decisionConfirmed(confirmation: DecisionConfirmation) {
    if (!!confirmation) {
      this.changeStatus(confirmation).pipe(
        switchMap(app => this.sendDecision(app.id, confirmation))
      ).subscribe(
          () => this.router.navigateByUrl('/workqueue'),
          error => this.notification.errorInfo(error));
    }
  }

  public resendDecision(): void {
    this.confirmDecisionSend('RESEND_EMAIL')
      .pipe(
        filter(result => !!result),
        switchMap(result => this.sendDecision(this.application.id, result)),
        tap(() => this.store.dispatch(new tagActions.Load()))
      ).subscribe(
        () => this.notification.success(findTranslation('decision.action.send')),
        error => this.notification.errorInfo(error));
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

  private confirmDecisionSend(type: string, status?: string): Observable<DecisionConfirmation> {
    const config = {...DECISION_MODAL_CONFIG};
    config.data.type = type;
    config.data.status = ApplicationStatus[status];
    config.data.distributionList = this.application.decisionDistributionList;

    const dialogRef = this.dialog.open<DecisionModalComponent>(DecisionModalComponent, config);
    return dialogRef.afterClosed();
  }

  private changeStatus(confirmation: DecisionConfirmation): Observable<Application> {
    const changeInfo = new StatusChangeInfo(undefined, confirmation.comment, confirmation.owner);
    return this.applicationStore.changeStatus(this.application.id, confirmation.status, changeInfo).pipe(
      tap(application => this.statusChanged(application))
    );
  }

  private statusChanged(application: Application): void {
    this.application = application;
    this.notification.success(findTranslation(['decision.type', this.application.status, 'confirmation']));
  }

  private sendDecision(appId: number, confirmation: DecisionConfirmation): Observable<{}> {
    return Some(confirmation.distributionList)
      .filter(distribution => distribution.length > 0)
      .map(distribution => new DecisionDetails(distribution, confirmation.emailMessage))
      .map(details => this.decisionService.sendDecision(appId, details).pipe(
        tap(() => this.application.decisionDistributionList = details.decisionDistributionList),
        catchError(error => this.notification.errorCatch(error, {}))
      ))
      .orElseGet(() => of({}));
  }
}
