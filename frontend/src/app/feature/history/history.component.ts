import {Component, Input, OnInit} from '@angular/core';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import * as fromProject from '../project/reducers';
import * as fromApplication from '../application/reducers';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {SetFieldsVisible} from './actions/history-actions';
import {takeUntil} from 'rxjs/internal/operators';
import {TimeUtil} from '../../util/time.util';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {MatLegacySlideToggleChange as MatSlideToggleChange} from '@angular/material/legacy-slide-toggle';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {
  @Input() targetType: ActionTargetType;

  meta$: Observable<StructureMeta>;
  changesToday$ = new BehaviorSubject<ChangeHistoryItem[]>([]);
  changesWithinWeek$ = new BehaviorSubject<ChangeHistoryItem[]>([]);
  olderChanges$ = new BehaviorSubject<ChangeHistoryItem[]>([]);
  fieldsVisible$: Observable<boolean>;
  loading$: Observable<boolean>;

  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    const target = this.targetType === ActionTargetType.Project
      ? fromProject
      : fromApplication;

    this.store.select(target.getHistory)
      .pipe(takeUntil(this.destroy))
      .subscribe(changes => this.splitByTime(changes));

    this.loading$ = this.store.select(target.getHistoryLoading);

    this.meta$ = this.store.select(target.getMeta);
    this.fieldsVisible$ = this.store.select(target.getFieldsVisible);
  }

  toggleFieldVisibility(toggleChange: MatSlideToggleChange): void {
    this.store.dispatch(new SetFieldsVisible(this.targetType, toggleChange.checked));
  }

  private splitByTime(changes: ChangeHistoryItem[]): void {
    const today = new Date();
    const weekBefore = TimeUtil.subract(new Date(), 7, 'day');

    const byTime = {
      today: [],
      withinWeek: [],
      older: []
    };

    changes.forEach(change => {
      if (TimeUtil.isSame(change.changeTime, today, 'day')) {
        byTime.today = byTime.today.concat(change);
      } else if (!TimeUtil.isBefore(change.changeTime, weekBefore, 'day')) {
        byTime.withinWeek = byTime.withinWeek.concat(change);
      } else {
        byTime.older = byTime.older.concat(change);
      }
    });

    this.changesToday$.next(byTime.today);
    this.changesWithinWeek$.next(byTime.withinWeek);
    this.olderChanges$.next(byTime.older);
  }
}
