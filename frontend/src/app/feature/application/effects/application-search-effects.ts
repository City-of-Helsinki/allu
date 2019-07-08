import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ApplicationService} from '@service/application/application.service';
import {Action} from '@ngrx/store';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {from, Observable, of} from 'rxjs';
import {
  ApplicationSearchActionType,
  Search,
  SearchByNameOrId,
  SearchFailed,
  SearchSuccess
} from '@feature/application/actions/application-search-actions';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ApplicationSearchEffects {
  constructor(private actions: Actions,
              private applicationService: ApplicationService) {}

  @Effect()
  applicationSearchByNameOrId: Observable<Action> = this.actions.pipe(
    ofType<SearchByNameOrId>(ApplicationSearchActionType.SearchByNameOrId),
    filter(action => action.payload && action.payload.length > 2),
    switchMap(action =>
      this.applicationService.nameOrApplicationIdSearch(action.payload).pipe(
        map(applications => new SearchSuccess(action.targetType, applications)),
        catchError(error => of(new SearchFailed(action.targetType, error)))
      )
    )
  );

  @Effect()
  applicationSearch: Observable<Action> = this.actions.pipe(
    ofType<Search>(ApplicationSearchActionType.Search),
    switchMap((action) =>
      this.applicationService.pagedSearch(action.payload.query, action.payload.sort, action.payload.pageRequest).pipe(
        map(searchResult => new SearchSuccess(action.targetType, searchResult)),
        catchError(error => from([
          new SearchFailed(action.targetType, error),
          new NotifyFailure(error)
        ]))
      ))
  );
}
