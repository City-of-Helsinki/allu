import {Injectable} from '@angular/core';
import {Actions, createEffect} from '@ngrx/effects';
import {Observable, of} from 'rxjs/index';
import {Action, Store} from '@ngrx/store';
import * as fromHistory from '../reducers/history-reducer';
import * as fromProject from '../../project/reducers';
import * as fromApplication from '../../application/reducers';
import {ofTargetAndType, withLatestExistingOfTargetAndType} from '../../allu/actions/action-with-target';
import {HistoryActionType, Load, LoadByTargetId, LoadFailed, LoadStatus, LoadStatusComplete, LoadSuccess} from '../actions/history-actions';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {HistoryService} from '../../../service/history/history-service';


@Injectable()
export class HistoryEffects {

  constructor(private actions: Actions,
              private store: Store<fromHistory.State>,
              private historyService: HistoryService) {}

  
  loadProjectHistory: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Load>(ActionTargetType.Project, this.currentProject, HistoryActionType.Load),
    switchMap(([action, project]) => this.historyService.getProjectHistory(project.id).pipe(
      map(history => new LoadSuccess(action.targetType, history)),
      catchError(error => of(new LoadFailed(action.targetType, error)))
    ))
  ));

  
  loadApplicationHistory: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestExistingOfTargetAndType<Load>(ActionTargetType.Application, this.currentApplication, HistoryActionType.Load),
    switchMap(([action, application]) => this.historyService.getApplicationHistory(application.id).pipe(
      switchMap(history => [
        new LoadSuccess(action.targetType, history),
        new LoadStatus(action.targetType, application.id)
      ]),
      catchError(error => of(new LoadFailed(action.targetType, error)))
    ))
  ));

  
  loadApplicationHistoryByTargetId: Observable<Action> = createEffect(() => this.actions.pipe(
    ofTargetAndType<LoadByTargetId>(ActionTargetType.Application, HistoryActionType.LoadByTargetId),
    switchMap((action) => this.historyService.getApplicationHistory(action.payload).pipe(
      switchMap(history => [
        new LoadSuccess(action.targetType, history),
        new LoadStatus(action.targetType, action.payload)
      ]),
      catchError(error => of(new LoadFailed(action.targetType, error)))
    ))
  ));

  
  loadStatusHistory: Observable<Action> = createEffect(() => this.actions.pipe(
    ofTargetAndType<LoadStatus>(ActionTargetType.Application, HistoryActionType.LoadStatus),
    switchMap(action => this.historyService.getStatusHistory(action.payload).pipe(
      map(statusHistory => new LoadStatusComplete(action.targetType, statusHistory)),
      catchError(error => of(new LoadStatusComplete(action.targetType, undefined, error)))
    ))
  ));

  private get currentApplication() {
    return this.store.select(fromApplication.getCurrentApplication);
  }

  private get currentProject() {
    return this.store.select(fromProject.getCurrentProject);
  }
}
