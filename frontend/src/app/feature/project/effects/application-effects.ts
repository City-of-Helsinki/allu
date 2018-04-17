import {Injectable} from '@angular/core';
import { Action, Store } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import {
  ApplicationActionTypes,
  Load,
  LoadSuccess,
  LoadFailed, AddSuccess, AddFailed, Add, RemoveSuccess, RemoveFailed
} from '../actions/application-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import * as fromProject from '../reducers';
import {ApplicationService} from '../../../service/application/application.service';
import {ProjectService} from '../../../service/project/project.service';

@Injectable()
export class ApplicationEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private applicationService: ApplicationService,
              private projectService: ProjectService) {}

  @Effect()
  loadApplications: Observable<Action> = this.actions.pipe(
    ofType<Load>(ApplicationActionTypes.Load),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) =>
      this.applicationService.byProject(project.id, payload.sort, payload.pageRequest)
        .pipe(
          map(page => new LoadSuccess(page)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );

  @Effect()
  addApplication: Observable<Action> = this.actions.pipe(
    ofType<Add>(ApplicationActionTypes.Add),
    map(action => action.payload),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) =>
      this.projectService.addProjectApplication(project.id, payload)
        .pipe(
          map((applications) => new AddSuccess(applications)),
          catchError(error => of(new AddFailed(error)))
        )
    )
  );

  @Effect()
  removeApplication: Observable<Action> = this.actions.pipe(
    ofType<Add>(ApplicationActionTypes.Remove),
    map(action => action.payload),
    switchMap(payload =>
      this.projectService.removeApplication(payload).pipe(
        map(() => new RemoveSuccess(payload)),
        catchError(error => of(new RemoveFailed(error)))
      )
    )
  );
}
