import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ApplicationService} from '@service/application/application.service';
import {Observable} from 'rxjs/internal/Observable';
import {Action} from '@ngrx/store';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {of} from 'rxjs/internal/observable/of';
import * as application from '@feature/application/actions/application-search-actions';
import {ApplicationSearchActionType} from '@feature/application/actions/application-search-actions';

@Injectable()
export class ApplicationSearchEffects {
  constructor(private actions: Actions,
              private applicationService: ApplicationService) {}

  @Effect()
  applicationSearchByNameOrId: Observable<Action> = this.actions.pipe(
    ofType<application.SearchByNameOrId>(ApplicationSearchActionType.SearchByNameOrId),
    filter(action => action.payload && action.payload.length > 2),
    switchMap(action =>
      this.applicationService.nameOrApplicationIdSearch(action.payload).pipe(
        map(applications => new application.SearchByNameOrIdSuccess(action.targetType, applications)),
        catchError(error => of(new application.SearchByNameOrIdFailed(action.targetType, error)))
      )
    )
  );
}
