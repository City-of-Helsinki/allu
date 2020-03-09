import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Observable, of} from 'rxjs';
import {Application} from '@model/application/application';
import {ApplicationStatus, inHandling} from '@model/application/application-status';
import {findTranslation} from '@util/translations';
import {NotificationService} from '@feature/notification/notification.service';
import {DECISION_MODAL_CONFIG, DecisionConfirmation, DecisionModalComponent} from './decision-modal.component';
import {DecisionService} from '@service/decision/decision.service';
import {DECISION_PROPOSAL_MODAL_CONFIG, DecisionProposalModalComponent} from './proposal/decision-proposal-modal.component';
import {ApplicationStore} from '@service/application/application-store';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Some} from '@util/option';
import {DecisionDetails} from '@model/decision/decision-details';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {select, Store} from '@ngrx/store';
import {Load} from '@feature/comment/actions/comment-actions';
import * as tagActions from '@feature/application/actions/application-tag-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/internal/operators';
import {ApplicationType, automaticDecisionMaking, requiresContract} from '@model/application/type/application-type';
import {BaseDecisionActionsComponent} from '@feature/decision/base-decision-actions.component';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

@Component({
  selector: 'decision-actions',
  templateUrl: './decision-actions.component.html',
  styleUrls: ['./decision-actions.component.scss']
})
export class DecisionActionsComponent extends BaseDecisionActionsComponent implements OnInit, OnChanges {
  @Input() allowActions: boolean;
  @Input() allowResend: boolean;
  @Input() application: Application;
  @Input() approvedOperationalCondition = false;
  @Input() tab: DecisionTab = DecisionTab.DECISION;
  @Output() onDecisionConfirm = new EventEmitter<StatusChangeInfo>();

  showProposal = false;
  skipProposal = false;
  showDecision = false;
  showToOperationalCondition = false;
  type: ApplicationType;

  constructor(private applicationStore: ApplicationStore,
              private myStore: Store<fromRoot.State>,
              private decisionService: DecisionService,
              private router: Router,
              private dialog: MatDialog,
              private notification: NotificationService) {
    super(myStore);
  }

  ngOnInit(): void {
    this.watchDecisionBlocked();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const status = this.application.status;
    this.showProposal = inHandling(status)
      && !automaticDecisionMaking(this.application.type)
      && !requiresContract(this.application.type);
    this.skipProposal = inHandling(status) && automaticDecisionMaking(this.application.type);
    this.showDecision = ApplicationStatus.DECISIONMAKING === status;
    this.showToOperationalCondition = this.approvedOperationalCondition && this.application.targetState === ApplicationStatus.DECISION;
    this.type = this.application.type;
  }

  public decisionProposal(proposalType: string): void {
    const config = {
      ...DECISION_PROPOSAL_MODAL_CONFIG,
      data: {
        proposalType,
        cityDistrict: this.application.firstLocation.effectiveCityDistrictId,
        applicationType: this.application.type
      }
    };

    this.dialog.open<DecisionProposalModalComponent>(DecisionProposalModalComponent, config).afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(proposal => this.proposalConfirmed(proposal));
  }

  public decision(): void {
    const status = this.application.targetState;
    this.confirmDecisionSend(status, status)
      .subscribe(result => this.decisionConfirmed(result));
  }

  public operationalCondition(): void {
    const status = ApplicationStatus.DECISION;
    this.confirmDecisionSend(status, status).pipe(
      switchMap(confirmation => this.toOperationalCondition(confirmation)),
      switchMap(confirmation => this.changeStatus(confirmation)),
    ).subscribe(app => {}); // Nothing to do but must subscribe so observable is run
  }

  public returnToPreparation(): void {
    this.confirmDecisionSend(ApplicationStatus.RETURNED_TO_PREPARATION, ApplicationStatus.RETURNED_TO_PREPARATION).pipe(
      switchMap(confirmation => this.changeStatus(confirmation))
    ).subscribe(
      () => this.router.navigateByUrl('/workqueue'),
      error => this.notification.errorInfo(error)
    );
  }

  public decisionConfirmed(confirmation: DecisionConfirmation) {
    this.changeStatus(confirmation).pipe(
      switchMap(app => this.sendDecision(app.id, confirmation))
    ).subscribe(
      () => this.router.navigateByUrl('/workqueue'),
      error => this.notification.errorInfo(error));
  }

  public resendDecision(): void {
    this.confirmDecisionSend('RESEND_EMAIL', this.application.status)
      .pipe(
        filter(result => !!result),
        switchMap(result => this.sendDecision(this.application.id, result)),
        tap(() => this.store.dispatch(new tagActions.Load()))
      ).subscribe(
        () => this.notification.success(findTranslation('decision.action.send')),
        error => this.notification.errorInfo(error));
  }

  public getDecisionReturnTextKey(): string {
    return this.tab === DecisionTab.TERMINATION ?
      'decision.type.TERMINATED.rejectDraft' :
      'decision.type.RETURNED_TO_PREPARATION.confirmText';
  }


  private proposalConfirmed(changeInfo: StatusChangeInfo) {
    if (changeInfo) {
      this.applicationStore.changeStatus(this.application.id, ApplicationStatus.DECISIONMAKING, changeInfo)
        .subscribe(() => {
          this.store.dispatch(new Load(ActionTargetType.Application));
          this.onDecisionConfirm.emit(changeInfo);
        }, err => this.notification.error(findTranslation('application.error.toDecisionmaking')));
    }
  }

  private confirmDecisionSend(type: string, status?: ApplicationStatus): Observable<DecisionConfirmation> {
    return this.store.pipe(
      select(fromApplication.getDistributionList),
      take(1),
      map(distributionList => ({
        ...DECISION_MODAL_CONFIG,
        data: {
          type,
          status,
          distributionList,
          tab: this.tab
        }
      })),
      switchMap(config => this.dialog.open<DecisionModalComponent>(DecisionModalComponent, config).afterClosed()),
      filter(result => !!result)
    );
  }

  private changeStatus(confirmation?: DecisionConfirmation): Observable<Application> {
    const changeInfo = new StatusChangeInfo(undefined, confirmation.comment, confirmation.owner);
    return this.applicationStore.changeStatus(this.application.id, confirmation.status, changeInfo).pipe(
      tap(application => this.statusChanged(application))
    );
  }

  private statusChanged(application: Application): void {
    this.application = application;
  }

  private sendDecision(appId: number, confirmation: DecisionConfirmation): Observable<{}> {
    return Some(confirmation.distributionList)
      .filter(distribution => distribution.length > 0)
      .map(distribution => new DecisionDetails(distribution, confirmation.emailMessage))
      .map(details => this.sendDecisionDocument(appId, details, confirmation.status).pipe(
        catchError(error => {
          this.store.dispatch(new Load(ActionTargetType.Application));
          return this.notification.errorCatch(error, {});
        })
      ))
      .orElseGet(() => of({}));
  }

  private sendDecisionDocument(appId: number, details: DecisionDetails, status: ApplicationStatus): Observable<{}> {
    if (status === ApplicationStatus.OPERATIONAL_CONDITION) {
      return this.decisionService.sendOperationalCondition(appId, details);
    } else if (status === ApplicationStatus.FINISHED) {
      return this.decisionService.sendWorkFinished(appId, details);
    } else if (status === ApplicationStatus.TERMINATED) {
      return this.decisionService.sendTermination(appId, details);
    } else {
      return this.decisionService.sendDecision(appId, details);
    }
  }

  /**
   * Change application status first to decision so all automatic operations related to that state change are handled.
   * After successful status change send decision document.
   * After successful document send return confirmation for operational condition change
   */
  private toOperationalCondition(confirmation: DecisionConfirmation): Observable<DecisionConfirmation> {
    return this.changeStatus(confirmation).pipe(
      switchMap(app => this.sendDecision(app.id, confirmation)),
      map(() => ({
          ...confirmation,
          status: ApplicationStatus.OPERATIONAL_CONDITION,
          comment: undefined
        })
      )
    );
  }
}
