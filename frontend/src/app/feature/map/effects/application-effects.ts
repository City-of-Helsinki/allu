import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, Store, select} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Observable, of} from 'rxjs';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {MapDataService} from '@service/map/map-data-service';
import {ApplicationActionType, Search, SearchSuccess} from '@feature/map/actions/application-actions';
import * as fromMap from '@feature/map/reducers';
import {MapSearchFilter} from '@service/map-search-filter';
import {MapLayer} from '@service/map/map-layer';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private mapDataService: MapDataService) {}

  @Effect()
  search: Observable<Action> = this.actions.pipe(
    ofType<Search>(ApplicationActionType.Search),
    withLatestFrom(this.store.pipe(select(fromMap.getSelectedApplicationLayers))),
    map(([action, layers]) => this.withApplicationTypes(action.payload, layers)),
    switchMap(mapFilter => this.mapDataService.applicationsByLocation(mapFilter).pipe(
      map(addresses => new SearchSuccess(addresses)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  private withApplicationTypes(baseFilter: MapSearchFilter, layers: MapLayer[] = []): MapSearchFilter {
    return {
      ...baseFilter,
      types: layers.map(layer => layer.applicationType)
    };
  }
}
