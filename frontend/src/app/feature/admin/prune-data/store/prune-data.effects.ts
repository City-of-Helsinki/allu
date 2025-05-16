import { Injectable } from '@angular/core';
import { Actions, ofType, createEffect } from '@ngrx/effects';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, withLatestFrom } from 'rxjs/operators';
import * as PruneDataActions from './prune-data.actions';
import { Store } from '@ngrx/store';
import { PruneDataItem } from '../models/prude-data-item.model';
import { selectCurrentTab } from './prune-data.selectors';

@Injectable()
export class PruneDataEffects {
  private applicationEndPoint = '/api/applications/anonymizable';
  private userEndpoint = '/api/user/anonymizable';

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
        action.tab === 'user_data' ? this.userEndpoint : this.applicationEndPoint;
    
        // pagination
        const params: any = {};
        if (action.page !== undefined) params.page = action.page;
        if (action.size !== undefined) params.size = action.size;
    
         // sort
        if (action.sortField && action.sortDirection) {
          params.sort = `${action.sortField},${action.sortDirection}`;
        }
    
        params.type = action.tab;

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

  deleteData$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PruneDataActions.deleteData),
      withLatestFrom(this.store.select(selectCurrentTab)),
      mergeMap(([action, currentTab]) => {
        const endpoint =
          currentTab === 'user_data' ? this.userEndpoint : this.applicationEndPoint;
        return this.http.patch<void>(`${endpoint}`, action.ids).pipe(
          map(() => PruneDataActions.deleteDataSuccess({ ids: action.ids })),
          catchError(error => of(PruneDataActions.deleteDataSuccess({ ids: action.ids })))
        );
      })
    )
  );
}