import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {from, Observable, of} from 'rxjs';
import {
  Add,
  AddMultiple,
  AddPending,
  AddSuccess,
  ApplicationActionTypes,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove,
  RemoveSuccess
} from '../actions/application-actions';
import * as historyActions from '../../history/actions/history-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import * as fromProject from '../reducers';
import {ApplicationService} from '@service/application/application.service';
import {ProjectService} from '@service/project/project.service';
import * as projectActions from '../actions/project-actions';
import {ProjectActionTypes, SaveSuccess} from '../actions/project-actions';
import {Clear} from '../actions/application-basket-actions';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {NumberUtil} from '@util/number.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ApplicationEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private applicationService: ApplicationService,
              private projectService: ProjectService) {}

  
  loadApplications: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ApplicationActionTypes.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    filter(([payload, project]) => NumberUtil.isExisting(project)),
    switchMap(([action, project]) =>
      this.projectService.getProjectApplications(project.id)
        .pipe(
          map(applications => new LoadSuccess(applications)),
          catchError(error => from([
            new LoadFailed(error),
            new NotifyFailure(error)
          ]))
        )
    )
  ));

  
  addApplication: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Add>(ApplicationActionTypes.Add),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) => this.addProjectApplications(project.id, [payload]))
  ));

  
  addApplications: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<AddMultiple>(ApplicationActionTypes.AddMultiple),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) => this.addProjectApplications(project.id, payload))
  ));

  
  removeApplication: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Remove>(ApplicationActionTypes.Remove),
    map(action => action.payload),
    switchMap(payload =>
      this.projectService.removeApplication(payload).pipe(
        switchMap(() => [
          new RemoveSuccess(payload),
          new historyActions.Load(ActionTargetType.Project)
        ]),
        catchError(error => of(new NotifyFailure(error)))
      )
    )
  ));

  
  projectUpdate: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType(ApplicationActionTypes.AddSuccess, ApplicationActionTypes.RemoveSuccess),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    map(([payload, project]) => new projectActions.Load(project.id))
  ));

  
  projectSaved: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SaveSuccess>(ProjectActionTypes.SaveSuccess),
    withLatestFrom(this.store.select(fromProject.getPendingApplicationIds)),
    map(([payload, ids]) => ids),
    filter(applications => applications.length >= 0),
    map(ids => new AddPending(ids))
  ));

  
  savePending: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<AddPending>(ApplicationActionTypes.AddPending),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) => this.projectService.addProjectApplications(project.id, payload).pipe(
      switchMap((applications) => {
        if (applications && applications.length) {
          // Only clear application basket when applications in basket
          // were added to project
          return [new AddSuccess(applications), new Clear()];
        } else {
          return [new AddSuccess(applications)];
        }
      }),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  private addProjectApplications(projectId: number, applicationIds: number[]): Observable<Action> {
    return this.projectService.addProjectApplications(projectId, applicationIds)
      .pipe(
        switchMap((applications) => [
          new AddSuccess(applications),
          new historyActions.Load(ActionTargetType.Project)
        ]),
        catchError(error => of(new NotifyFailure(error)))
      );
  }
}
