import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Observable, of} from 'rxjs';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {MapDataService} from '@service/map/map-data-service';
import {ApplicationActionType, Search, SearchSuccess} from '@feature/map/actions/application-actions';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private mapDataService: MapDataService) {}

  @Effect()
  search: Observable<Action> = this.actions.pipe(
    ofType<Search>(ApplicationActionType.Search),
    switchMap(action => this.mapDataService.applicationsByLocation(action.payload).pipe(
      map(addresses => new SearchSuccess(addresses)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );
}
