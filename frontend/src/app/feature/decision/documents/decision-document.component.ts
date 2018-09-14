import {Component, OnDestroy, OnInit} from '@angular/core';
import {ApplicationStore} from '@service/application/application-store';
import {combineLatest, Observable, Subject} from 'rxjs/index';
import {Store} from '@ngrx/store';
import {map, takeUntil} from 'rxjs/internal/operators';
import {Application} from '@model/application/application';
import {Load} from '@feature/decision/actions/decision-actions';
import * as fromDecision from '@feature/decision/reducers';
import {ActivatedRoute} from '@angular/router';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationStatus} from '@model/application/application-status';

@Component({
  selector: 'decision-document',
  templateUrl: './decision-document.component.html',
  styleUrls: ['./decision-document.component.scss']
})
export class DecisionDocumentComponent implements OnInit, OnDestroy {
  applicationChanges$: Observable<Application>;

  pdf$: Observable<Blob>;
  loading$: Observable<boolean>;
  processing$: Observable<boolean>;
  tab$: Observable<string>;
  showDecisionActions$: Observable<boolean>;
  showContractActions$: Observable<boolean>;

  private destroy = new Subject<boolean>();

  constructor(
    private route: ActivatedRoute,
    private store: Store<fromDecision.State>,
    private applicationStore: ApplicationStore) {}

  ngOnInit(): void {
    this.applicationChanges$ = this.applicationStore.application;
    this.pdf$ = this.store.select(fromDecision.getPdf);
    this.loading$ = this.store.select(fromDecision.getLoading);
    this.tab$ = this.store.select(fromDecision.getTab).pipe(map(tab => DecisionTab[tab]));
    this.processing$ = this.applicationStore.changes.pipe(
      map(change => change.processing),
      takeUntil(this.destroy)
    );

    this.showDecisionActions$ = combineLatest(
      this.store.select(fromDecision.showDecisionActions),
      this.applicationChanges$
    ).pipe(map(([show, app]) => this.showDecisionActions(show, app)));

    this.showContractActions$ = combineLatest(
      this.store.select(fromDecision.showContractActions),
      this.applicationChanges$
    ).pipe(map(([show, app]) => this.showContractActions(show, app)));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onDecisionConfirm(): void {
    this.store.dispatch(new Load());
  }

  private showDecisionActions(show: boolean, app: Application): boolean {
    const showByType = app.type !== ApplicationType.PLACEMENT_CONTRACT;
    const showByStatus = app.statusEnum >= ApplicationStatus.DECISIONMAKING;
    return show && (showByType || showByStatus);
  }

  private showContractActions(show: boolean, app: Application): boolean {
    const showByType = app.type === ApplicationType.PLACEMENT_CONTRACT;
    const showByStatus = [ApplicationStatus.HANDLING, ApplicationStatus.RETURNED_TO_PREPARATION].indexOf(app.statusEnum) >= 0;
    return show && showByType && showByStatus;
  }
}
