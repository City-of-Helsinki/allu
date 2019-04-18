import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {Observable} from 'rxjs/internal/Observable';
import {defer} from 'rxjs/internal/observable/defer';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {MapLayer} from '@service/map/map-layer';
import {Control} from 'leaflet';
import {AddLayers, AddTreeStructure, MapLayerActionType, SelectLayers} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {MapLayerService} from '@feature/map/map-layer.service';
import {MapStore} from '@service/map/map-store';
import LayersObject = Control.LayersObject;

@Injectable()
export class MapLayerEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private layerService: MapLayerService,
              private mapStore: MapStore) {
  }

  @Effect()
  initAvailableLayers: Observable<Action> = defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    map(() => this.getMapLayers()),
    switchMap(layers => [
      new AddLayers(ActionTargetType.Home, layers),
      new AddLayers(ActionTargetType.Location, layers)
    ])
  ));

  @Effect()
  initMapLayerTree: Observable<Action> = defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => this.layerService.createLayerTreeStructure()),
    switchMap(structure => [
      new AddTreeStructure(ActionTargetType.Home, structure),
      new AddTreeStructure(ActionTargetType.Location, structure)
    ])
  ));

  @Effect({dispatch: false})
  layersSelected: Observable<Action> = this.actions.pipe(
    ofType<SelectLayers>(MapLayerActionType.SelectLayers),
    tap((action: SelectLayers) => {
      if (action.targetType === ActionTargetType.Location) {
        this.mapStore.locationSearchFilterChange({layers: action.payload});
      } else {
        this.mapStore.mapSearchFilterChange({layers: action.payload});
      }
    })
  );

  private getMapLayers(): MapLayer[] {
    return [
      ...this.toMapLayers(this.layerService.createOverlays()),
      ...this.layerService.contentLayers,
      ...this.toMapLayers(this.layerService.winkkiRoadWorks),
      ...this.toMapLayers(this.layerService.winkkiEvents),
      ...this.toMapLayers(this.layerService.other),
      ...this.toMapLayers(this.layerService.createRestrictedOverlays())
      ];
  }

  private toMapLayers(layersObject: LayersObject): MapLayer[] {
    return Object.keys(layersObject).map(k => new MapLayer(k, layersObject[k]));
  }
}
