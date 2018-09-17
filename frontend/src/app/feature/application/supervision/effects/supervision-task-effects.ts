import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {SupervisionTaskService} from '@service/supervision/supervision-task.service';
import {from, Observable, of} from 'rxjs/index';
import {
  Approve,
  ApproveSuccess,
  Load,
  LoadFailed,
  LoadSuccess,
  Reject, RejectSuccess,
  Remove,
  RemoveSuccess,
  Save,
  SaveSuccess,
  SupervisionTaskActionType
} from '@feature/application/supervision/actions/supervision-task-actions';
import * as TagActions from '@feature/application/actions/application-tag-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';

const requiresTagReload = [
  SupervisionTaskActionType.SaveSuccess,
  SupervisionTaskActionType.RemoveSuccess,
  SupervisionTaskActionType.ApproveSuccess,
  SupervisionTaskActionType.RejectSuccess,
];

@Injectable()
export class SupervisionTaskEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private taskService: SupervisionTaskService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<Load>(SupervisionTaskActionType.Load),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.taskService.findTasksByApplicationId(app.id).pipe(
      map(tasks => new LoadSuccess(tasks)),
      catchError(error => from([
        new LoadFailed(),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  save: Observable<Action> = this.actions.pipe(
    ofType<Save>(SupervisionTaskActionType.Save),
    switchMap(action => this.taskService.save(action.payload).pipe(
      switchMap(task => [
        new SaveSuccess(task),
        new NotifySuccess('supervision.task.action.save')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  remove: Observable<Action> = this.actions.pipe(
    ofType<Remove>(SupervisionTaskActionType.Remove),
    switchMap(action => this.taskService.remove(action.payload).pipe(
      switchMap(() => [
        new RemoveSuccess(action.payload),
        new NotifySuccess('supervision.task.action.remove')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  approve: Observable<Action> = this.actions.pipe(
    ofType<Approve>(SupervisionTaskActionType.Approve),
    switchMap(action => this.taskService.approve(action.payload).pipe(
      switchMap((task) => [
        new ApproveSuccess(task),
        new NotifySuccess('supervision.task.action.approve')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  reject: Observable<Action> = this.actions.pipe(
    ofType<Reject>(SupervisionTaskActionType.Reject),
    switchMap(action => this.taskService.reject(action.payload.task, action.payload.newSupervisionDate).pipe(
      switchMap((task) => [
        new RejectSuccess(task),
        new NotifySuccess('supervision.task.action.reject')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  reloadTags: Observable<Action> = this.actions.pipe(
    ofType<Action>(...requiresTagReload),
    map(() => new TagActions.Load())
  );
}
