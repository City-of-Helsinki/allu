import {catchError, map, switchMap} from 'rxjs/operators';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Injectable} from '@angular/core';
import * as fromProject from '../reducers';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import {Action, Store} from '@ngrx/store';
import {ProjectService} from '../../../service/project/project.service';
import {Load, LoadFailed, LoadSuccess, ProjectActionTypes} from '../actions/project-actions';

@Injectable()
export class ProjectEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService) {
  }

  @Effect()
  loadApplications: Observable<Action> = this.actions.pipe(
    ofType<Load>(ProjectActionTypes.Load),
    map(action => action.payload),
    switchMap(payload =>
      this.projectService.getProject(payload)
        .pipe(
          map(applications => new LoadSuccess(applications)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );
}
