import {Component, Input, OnInit} from '@angular/core';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import * as fromProject from '../project/reducers';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {Load} from './actions/history-actions';
import {takeUntil} from 'rxjs/internal/operators';
import {TimeUtil} from '../../util/time.util';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: []
})
export class HistoryComponent implements OnInit {
  @Input() targetType: ActionTargetType;
  changesToday$ = new BehaviorSubject<ChangeHistoryItem[]>([]);
  changesWithinWeek$ = new BehaviorSubject<ChangeHistoryItem[]>([]);
  olderChanges$ = new BehaviorSubject<ChangeHistoryItem[]>([]);

  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    if (this.targetType === ActionTargetType.Project) {
      this.store.dispatch(new Load(this.targetType));
      this.store.select(fromProject.getHistory)
        .pipe(takeUntil(this.destroy))
        .subscribe(changes => this.splitByTime(changes));
    } else {
      // No implementation for applications yet
    }
  }

  private splitByTime(changes: ChangeHistoryItem[]): void {
    const today = new Date();
    const week = TimeUtil.add(new Date(), 7, 'day');

    const byTime = {
      today: [],
      withinWeek: [],
      older: []
    };

    changes.forEach(change => {
      if (TimeUtil.isSame(change.changeTime, today, 'day')) {
        byTime.today = byTime.today.concat(change);
      } else if (!TimeUtil.isAfter(change.changeTime, week)) {
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
