import {Injectable} from '@angular/core';
import {Actions, createEffect} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Observable, of} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {MapDataService} from '@service/map/map-data-service';
import {ApplicationActionType, Search, SearchSuccess} from '@feature/map/actions/application-actions';
import * as fromMap from '@feature/map/reducers';
import * as fromLocation from '@feature/application/location/reducers';
import {MapSearchFilter} from '@service/map-search-filter';
import {MapLayer} from '@service/map/map-layer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {withLatestOfTargetAndType} from '@feature/allu/actions/action-with-target';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private mapDataService: MapDataService) {}

  
  mapSearch: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestOfTargetAndType<Search>(ActionTargetType.Home,
      this.store.pipe(select(fromMap.getSelectedApplicationLayers)),
      ApplicationActionType.Search),
    switchMap(([action, layers]) => this.loadByType(action.targetType, action.payload, layers))
  ));

  
  locationSearch: Observable<Action> = createEffect(() => this.actions.pipe(
    withLatestOfTargetAndType<Search>(ActionTargetType.Location,
      this.store.pipe(select(fromLocation.getSelectedApplicationLayers)),
      ApplicationActionType.Search),
    switchMap(([action, layers]) => this.loadByType(action.targetType, action.payload, layers))
  ));

  private loadByType(type: ActionTargetType, baseFilter: MapSearchFilter, layers: MapLayer[]): Observable<Action> {
    const searchFilter = this.withApplicationTypes(baseFilter, layers);
    const includeSurveyRequired = type === ActionTargetType.Location;
    return this.mapDataService.applicationsByLocation(searchFilter, includeSurveyRequired).pipe(
      map(applications => new SearchSuccess(type, applications)),
      catchError(error => of(new NotifyFailure(error)))
    );
  }

  private withApplicationTypes(baseFilter: MapSearchFilter, layers: MapLayer[] = []): MapSearchFilter {
    return {
      ...baseFilter,
      types: layers.map(layer => layer.applicationType)
    };
  }
}
