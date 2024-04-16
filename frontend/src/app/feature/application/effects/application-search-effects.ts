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
import {
  ApplicationActionType,
  ChangeOwnerSuccess,
  RemoveOwnerNotificationSuccess,
  RemoveOwnerSuccess
} from '@feature/application/actions/application-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import * as fromAuth from '@feature/auth/reducers';
import {ObjectUtil} from '@util/object.util';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {ApproveComplete, BulkApprovalActionType} from '@feature/decision/actions/bulk-approval-actions';

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
    switchMap(action => this.setTargetTypeSpecificParameters(action)),
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
  refreshWorkQueueSearch: Observable<Action> = this.actions.pipe(
    ofType<ChangeOwnerSuccess | RemoveOwnerSuccess | RemoveOwnerNotificationSuccess | ApproveComplete>(
      ApplicationActionType.ChangeOwnerSuccess,
      ApplicationActionType.RemoveOwnerSuccess,
      ApplicationActionType.RemoveOwnerNotificationSuccess,
      BulkApprovalActionType.ApproveComplete
    ),
    switchMap(() => this.getCurrentWorkQueueSearch()),
    switchMap(([search, sort, pageRequest]) => [
      new Search(ActionTargetType.ApplicationWorkQueue, search, sort, pageRequest),
      new ClearSelected(ActionTargetType.ApplicationWorkQueue)
    ])
  );

  private getCurrentWorkQueueSearch(): Observable<[ApplicationSearchQuery, Sort, PageRequest]> {
    return combineLatest([
      this.store.pipe(select(fromWorkQueue.getApplicationSearchParameters), filter(params => !!params)),
      this.store.pipe(select(fromWorkQueue.getApplicationSearchSort)),
      this.store.pipe(select(fromWorkQueue.getApplicationSearchPageRequest)),
    ]);
  }

  /**
   * In case of Application workqueue current tab must be checked. When current tab is user's own tab
   * query should be updated to include only current users applications.
   */
  private setTargetTypeSpecificParameters(action: Search): Observable<Search> {
    if (action.targetType === ActionTargetType.ApplicationWorkQueue) {
      return combineLatest(
        this.store.pipe(select(fromWorkQueue.getTab)),
        this.store.pipe(select(fromAuth.getUser), filter(user => !!user))
      ).pipe(
        map(([tab, user]) => {
          const payload = action.payload;
          const queryCopy = ObjectUtil.clone(payload.query);
          queryCopy.owner = WorkQueueTab.OWN === tab ? [user.userName] : payload.query.owner;
          return new Search(action.targetType, queryCopy, payload.sort, payload.pageRequest);
        })
      );
    } else {
      return of(action);
    }
  }
}
