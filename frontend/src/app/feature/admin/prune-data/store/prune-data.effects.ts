import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, withLatestFrom } from 'rxjs/operators';
import * as PruneDataActions from './prune-data.actions';
import { Action, Store } from '@ngrx/store';
import { selectCurrentTab } from './prune-data.selectors';

@Injectable()
export class PruneDataEffects {
  private applicationsAnonymizableEndpoint  = '/api/applications/anonymizable';
  private applicationsAnonymizeEndpoint  = '/api/applications/anonymize';
  private customersEndpoint = '/api/customers/';
  private customersDeletableEndpoint = '/api/customers/deletable';

  constructor(
    private actions$: Actions,
    private http: HttpClient,
    private store: Store
  ) {}

  fetchAllData$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PruneDataActions.fetchAllData),
      switchMap(action => {
        const endpoint =
        action.tab === 'user_data' ? this.customersDeletableEndpoint : this.applicationsAnonymizableEndpoint;

        // pagination
        const params: any = {};
        if (action.page !== undefined) { params.page = action.page; }
        if (action.size !== undefined) { params.size = action.size; }

         // sort
        if (action.sortField && action.sortDirection) {
          params.sort = `${action.sortField},${action.sortDirection}`;
        }

        if (action.tab !== 'user_data') {
          params.type = action.tab;
        }

        return this.http.get<any>(endpoint, { params }).pipe(
          map(response => {
            if (response.content) {
              return PruneDataActions.fetchAllDataSuccess({
                  data: response.content,
                  totalItems: response.totalElements
                });
              } else {
                  return PruneDataActions.fetchAllDataSuccess({
                    data: response,
                    totalItems: response.length
                  });
                }
           }),
            catchError(error => of(PruneDataActions.fetchAllDataFailure({ error })))
        );
      })
    )
  );

  deleteData$ = createEffect((): Observable<Action> =>
    this.actions$.pipe(
      ofType(PruneDataActions.deleteData),
      withLatestFrom(this.store.select(selectCurrentTab)),
      mergeMap(([action, currentTab]) => {
        const endpoint =
          currentTab === 'user_data' ? this.customersEndpoint : this.applicationsAnonymizeEndpoint;
        const request$: Observable<unknown> = currentTab === 'user_data'
          ? this.http.delete(endpoint, { body: action.ids })
          : this.http.patch<void>(endpoint, action.ids);
        return request$.pipe(
          map(() => PruneDataActions.deleteDataSuccess({ ids: action.ids })),
          catchError(error => of(PruneDataActions.deleteDataFailure({ ids: action.ids, error })))
        );
      })
    )
  );
}
