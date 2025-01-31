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
  private endpoint = '/api/applications/anonymizable';
  private userEndpoint = '/api/anonymizable/user';

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
          action.tab === 'user_data' ? this.userEndpoint : this.endpoint;
        return this.http.get<PruneDataItem[]>(endpoint).pipe(
          map(data => PruneDataActions.fetchAllDataSuccess({ data })),
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
          currentTab === 'user_data' ? this.userEndpoint : this.endpoint;
        return this.http.post<void>(`${endpoint}`, { ids: action.ids }).pipe(
          map(() => PruneDataActions.deleteDataSuccess({ ids: action.ids })),
          catchError(error => of(PruneDataActions.deleteDataSuccess({ ids: action.ids })))
        );
      })
    )
  );
}