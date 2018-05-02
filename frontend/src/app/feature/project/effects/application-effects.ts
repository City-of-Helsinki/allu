import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import {
  Add,
  AddFailed,
  AddMultiple,
  AddSuccess,
  ApplicationActionTypes,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove,
  RemoveFailed,
  RemoveSuccess
} from '../actions/application-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import * as fromProject from '../reducers';
import {ApplicationService} from '../../../service/application/application.service';
import {ProjectService} from '../../../service/project/project.service';
import * as projectActions from '../actions/project-actions';

@Injectable()
export class ApplicationEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private applicationService: ApplicationService,
              private projectService: ProjectService) {}

  @Effect()
  loadApplications: Observable<Action> = this.actions.pipe(
    ofType<Load>(ApplicationActionTypes.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([action, project]) =>
      this.projectService.getProjectApplications(project.id)
        .pipe(
          map(applications => new LoadSuccess(applications)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );

  @Effect()
  addApplication: Observable<Action> = this.actions.pipe(
    ofType<Add>(ApplicationActionTypes.Add),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) => this.addProjectApplications(project.id, [payload]))
  );

  @Effect()
  addApplications: Observable<Action> = this.actions.pipe(
    ofType<AddMultiple>(ApplicationActionTypes.AddMultiple),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) => this.addProjectApplications(project.id, payload))
  );

  @Effect()
  removeApplication: Observable<Action> = this.actions.pipe(
    ofType<Remove>(ApplicationActionTypes.Remove),
    map(action => action.payload),
    switchMap(payload =>
      this.projectService.removeApplication(payload).pipe(
        map(() => new RemoveSuccess(payload)),
        catchError(error => of(new RemoveFailed(error)))
      )
    )
  );

  @Effect()
  projectUpdate: Observable<Action> = this.actions.pipe(
    ofType(ApplicationActionTypes.AddSuccess, ApplicationActionTypes.RemoveSuccess),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    map(([payload, project]) => new projectActions.Load(project.id))
  );

  private addProjectApplications(projectId: number, applicationIds: number[]): Observable<Action> {
    return this.projectService.addProjectApplications(projectId, applicationIds)
      .pipe(
        map((applications) => new AddSuccess(applications)),
        catchError(error => of(new AddFailed(error)))
      );
  }
}
