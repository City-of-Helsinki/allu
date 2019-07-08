import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ApplicationService} from '@service/application/application.service';
import {Action, select, Store} from '@ngrx/store';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {combineLatest, from, Observable, of} from 'rxjs';
import * as fromRoot from '@feature/allu/reducers';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import {
  ApplicationSearchActionType,
  ClearSelected,
  Search,
  SearchByNameOrId,
  SearchFailed,
  SearchSuccess
} from '@feature/application/actions/application-search-actions';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {ApplicationActionType, ChangeOwnerSuccess, RemoveOwnerSuccess} from '@feature/application/actions/application-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';

@Injectable()
export class ApplicationSearchEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
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

  @Effect()
  onOwnerChanges: Observable<Action> = this.actions.pipe(
    ofType<ChangeOwnerSuccess | RemoveOwnerSuccess>(ApplicationActionType.ChangeOwnerSuccess, ApplicationActionType.RemoveOwnerSuccess),
    switchMap(() => this.getCurrentWorkQueueSearch()),
    switchMap(([search, sort, pageRequest]) => [
      new Search(ActionTargetType.ApplicationWorkQueue, search, sort, pageRequest),
      new ClearSelected(ActionTargetType.ApplicationWorkQueue)
    ])
  );

  private getCurrentWorkQueueSearch(): Observable<[ApplicationSearchQuery, Sort, PageRequest]> {
    return combineLatest(
      this.store.pipe(select(fromWorkQueue.getApplicationSearchParameters), filter(search => !!search)),
      this.store.pipe(select(fromWorkQueue.getApplicationSearchSort)),
      this.store.pipe(select(fromWorkQueue.getApplicationSearchPageRequest)),
    );
  }
}
