import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ProjectService} from '../../../service/project/project.service';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import {Observable} from 'rxjs/Observable';
import {ChildProjectActionType, Load, LoadFailed, LoadSuccess} from '../actions/child-project-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';

@Injectable()
export class ChildProjectEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService) {}

  @Effect()
  loadParents: Observable<Action> = this.actions.pipe(
    ofType<Load>(ChildProjectActionType.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([payload, project]) =>
      this.projectService.getChildProjects(project.id)
        .pipe(
          map(children => new LoadSuccess(children)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );
}
