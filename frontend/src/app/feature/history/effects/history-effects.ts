import {Injectable} from '@angular/core';
import {Actions, Effect} from '@ngrx/effects';
import {Observable, of} from 'rxjs/index';
import {Action, Store} from '@ngrx/store';
import * as fromHistory from '../reducers/history-reducer';
import * as fromProject from '../../project/reducers';
import * as fromApplication from '../../application/reducers';
import {ofTargetAndType} from '../../allu/actions/action-with-target';
import {HistoryActionType, Load, LoadFailed, LoadSuccess} from '../actions/history-actions';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {HistoryService} from '../../../service/history/history-service';


@Injectable()
export class HistoryEffects {

  constructor(private actions: Actions,
              private store: Store<fromHistory.State>,
              private historyService: HistoryService) {}

  @Effect()
  loadProjectHistory: Observable<Action> = this.actions.pipe(
    ofTargetAndType<Load>(ActionTargetType.Project, this.currentProject, HistoryActionType.Load),
    switchMap(([action, project]) => this.historyService.getProjectHistory(project.id).pipe(
      map(history => new LoadSuccess(action.targetType, history)),
      catchError(error => of(new LoadFailed(action.targetType, error)))
    ))
  );

  @Effect()
  loadApplicationHistory: Observable<Action> = this.actions.pipe(
    ofTargetAndType<Load>(ActionTargetType.Application, this.currentApplication, HistoryActionType.Load),
    switchMap(([action, application]) => this.historyService.getApplicationHistory(application.id).pipe(
      map(history => new LoadSuccess(action.targetType, history)),
      catchError(error => of(new LoadFailed(action.targetType, error)))
    ))
  );

  private get currentApplication() {
    return this.store.select(fromApplication.getCurrentApplication);
  }

  private get currentProject() {
    return this.store.select(fromProject.getCurrentProject);
  }
}
