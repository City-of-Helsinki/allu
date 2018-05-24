import {catchError, filter, map, switchMap, tap} from 'rxjs/operators';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Injectable} from '@angular/core';
import * as fromProject from '../reducers';
import {Observable, of} from 'rxjs';
import {Action, Store} from '@ngrx/store';
import {ProjectService} from '../../../service/project/project.service';
import {
  Load,
  LoadFailed,
  LoadSuccess,
  ProjectActionTypes,
  Save,
  SaveFailed,
  SaveSuccess
} from '../actions/project-actions';
import {Router} from '@angular/router';

@Injectable()
export class ProjectEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService,
              private router: Router) {
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

  @Effect()
  save: Observable<Action> = this.actions.pipe(
    ofType<Save>(ProjectActionTypes.Save),
    map(action => action.payload),
    switchMap(project =>
      this.projectService.save(project).pipe(
        map(saved => new SaveSuccess(saved)),
        catchError(error => of(new SaveFailed(error)))
      )
    )
  );

  @Effect({dispatch: false})
  navigateAfterSave = this.actions.pipe(
    ofType<SaveSuccess>(ProjectActionTypes.SaveSuccess),
    map(action => action.payload),
    tap(project => this.router.navigate(['/projects', project.id]))
  );
}
