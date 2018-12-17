import {Injectable} from '@angular/core';
import {Actions, Effect} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromAuth from '@feature/auth/reducers';
import {Observable} from 'rxjs/internal/Observable';
import {defer} from 'rxjs/internal/observable/defer';
import {filter, map, switchMap} from 'rxjs/operators';
import {MapLayer} from '@service/map/map-layer';
import {Control} from 'leaflet';
import {FeatureGroupsObject} from '@model/map/feature-groups-object';
import {AddLayers, AddTreeStructure} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import LayersObject = Control.LayersObject;
import {MapLayerService} from '@feature/map/map-layer.service';

@Injectable()
export class MapLayerEffects {
  constructor(private actions: Actions, private store: Store<fromAuth.State>, private layerService: MapLayerService) {
  }

  @Effect()
  initAvailableLayers: Observable<Action> = defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => this.getMapLayers()),
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


  private getMapLayers(): Observable<MapLayer[]> {
    const layers = [
      ...this.toMapLayers(this.layerService.overlays),
      ...this.toMapLayers(this.layerService.contentLayers),
      ...this.toMapLayers(this.layerService.winkkiRoadWorks),
      ...this.toMapLayers(this.layerService.winkkiEvents),
      ...this.toMapLayers(this.layerService.other)
      ];
    return this.layerService.restrictedOverlays.pipe(
      map(restricted => this.toMapLayers(restricted)),
      map(restricted => ([
        ...restricted,
        ... layers
      ]))
    );
  }

  private toMapLayers(layersObject: LayersObject | FeatureGroupsObject): MapLayer[] {
    return Object.keys(layersObject).map(k => new MapLayer(k, layersObject[k]));
  }
}
