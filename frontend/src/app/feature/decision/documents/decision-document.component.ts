import {Component, OnDestroy, OnInit} from '@angular/core';
import {ApplicationStore} from '@service/application/application-store';
import {Observable, Subject} from 'rxjs/index';
import {Store} from '@ngrx/store';
import {map, switchMap, takeUntil} from 'rxjs/internal/operators';
import {Application} from '@model/application/application';
import {Load} from '@feature/decision/actions/decision-actions';
import * as fromApplication from '@feature/application/reducers';
import * as fromDecision from '@feature/decision/reducers';
import {ActivatedRoute} from '@angular/router';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

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
  showActions$: Observable<boolean>;
  tab$: Observable<string>;

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

    this.showActions$ = this.store.select(fromDecision.getShowActions);
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  onDecisionConfirm(): void {
    this.store.dispatch(new Load());
  }
}
