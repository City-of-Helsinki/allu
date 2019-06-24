import {Component, OnDestroy, OnInit} from '@angular/core';
import {ApplicationStore} from '@service/application/application-store';
import {Observable, Subject} from 'rxjs/index';
import {select, Store} from '@ngrx/store';
import {map, take, takeUntil} from 'rxjs/internal/operators';
import {Application} from '@model/application/application';
import {Load} from '@feature/decision/actions/decision-actions';
import * as fromApplication from '@feature/application/reducers';
import * as fromSupervision from '@feature/application/supervision/reducers';
import * as fromDecision from '@feature/decision/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {ActivatedRoute} from '@angular/router';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'decision-document',
  templateUrl: './decision-document.component.html',
  styleUrls: ['./decision-document.component.scss']
})
export class DecisionDocumentComponent implements OnInit, OnDestroy {
  application$: Observable<Application>;

  pdf$: Observable<Blob>;
  loading$: Observable<boolean>;
  processing$: Observable<boolean>;
  showDecisionActions$: Observable<boolean>;
  showContractActions$: Observable<boolean>;
  approvedOperationalCondition$: Observable<boolean>;
  hasInvoicing$: Observable<boolean>;

  private destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private store: Store<fromDecision.State>,
    private applicationStore: ApplicationStore) {}

  ngOnInit(): void {
    this.application$ = this.store.pipe(select(fromApplication.getCurrentApplication));
    this.store.pipe(select(fromDecision.getTab), take(1)).subscribe(tab => this.initContentByTab(tab));
    this.processing$ = this.applicationStore.changes.pipe(
      map(change => change.processing),
      takeUntil(this.destroy)
    );
    this.approvedOperationalCondition$ = this.store.pipe(
      select(fromSupervision.hasTask(SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskStatusType.APPROVED))
    );
    this.hasInvoicing$ = this.store.pipe(
      select(fromInvoicing.getChargeBasisEntryTotal),
      map(total => total > 0)
    );
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  initContentByTab(tab: DecisionTab): void {
    switch (tab) {
      case DecisionTab.CONTRACT: {
        this.pdf$ = this.store.select(fromDecision.getContractPdf);
        this.loading$ = this.store.select(fromDecision.getContractLoading);
        this.showContractActions$ = this.application$.pipe(map(app => this.showContractActions(app)));
        break;
      }

      case DecisionTab.OPERATIONAL_CONDITION: {
        this.pdf$ = this.store.select(fromDecision.getOperationalConditionApprovalPdf);
        this.loading$ = this.store.select(fromDecision.getOperationalConditionApprovalLoading);
        this.showDecisionActions$ = this.application$.pipe(map(app => this.showOperationalConditionActions(app)));
        break;
      }

      case DecisionTab.WORK_FINISHED: {
        this.pdf$ = this.store.select(fromDecision.getWorkFinishedApprovalPdf);
        this.loading$ = this.store.select(fromDecision.getWorkFinishedApprovaLoading);
        this.showDecisionActions$ = this.application$.pipe(map(app => this.showWorkFinishedActions(app)));
        break;
      }

      default: {
        this.pdf$ = this.store.select(fromDecision.getDecisionPdf);
        this.loading$ = this.store.select(fromDecision.getDecisionLoading);
        this.showDecisionActions$ = this.application$.pipe(map(app => this.showDecisionActions(app)));
        break;
      }
    }
  }

  onDecisionConfirm(): void {
    this.store.dispatch(new Load());
  }

  private showDecisionActions(app: Application): boolean {
    const inHandling = ApplicationStatus.HANDLING === app.status;
    const showByStatus = ArrayUtil.contains([app.status, app.targetState], ApplicationStatus.DECISION);
    return inHandling || showByStatus;
  }

  private showOperationalConditionActions(app: Application): boolean {
    const showByType = app.type === ApplicationType.EXCAVATION_ANNOUNCEMENT;
    const showByStatus = ArrayUtil.contains([app.status, app.targetState], ApplicationStatus.OPERATIONAL_CONDITION);
    return showByType && showByStatus;
  }

  private showWorkFinishedActions(app: Application): boolean {
    const showByType = [ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL].indexOf(app.type) >= 0;
    const showByStatus = ArrayUtil.contains([app.status, app.targetState], ApplicationStatus.FINISHED);
    return showByType && showByStatus;
  }

  private showContractActions(app: Application): boolean {
    const showByType = app.type === ApplicationType.PLACEMENT_CONTRACT;
    const showByStatus = ArrayUtil.contains(
      [ApplicationStatus.HANDLING, ApplicationStatus.RETURNED_TO_PREPARATION, ApplicationStatus.WAITING_CONTRACT_APPROVAL],
      app.status);
    return showByType && showByStatus;
  }
}
