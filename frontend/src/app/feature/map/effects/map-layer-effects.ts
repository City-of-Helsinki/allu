import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
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
import {ConfigService} from '@service/config/config.service';
import {createLayerTree} from '@feature/map/map-layer-tree';
import LayersObject = Control.LayersObject;
import { CurrentUser } from '@app/service/user/current-user';
import { forkJoin } from 'rxjs';
import { RoleType } from '@app/model/user/role-type';

const allowedRolesToSeeUndergroundAndCableLayers = [
  RoleType.ROLE_CREATE_APPLICATION, 
  RoleType.ROLE_PROCESS_APPLICATION, 
  RoleType.ROLE_SUPERVISE, 
  RoleType.ROLE_DECISION,
  RoleType.ROLE_DECLARANT, 
  RoleType.ROLE_MANAGE_SURVEY 
];

@Injectable()
export class MapLayerEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private layerService: MapLayerService,
              private configService: ConfigService,
              private mapStore: MapStore,
              private user: CurrentUser) {
  }
  
  initAvailableLayers: Observable<Action> = createEffect(() => defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    map(() => this.getMapLayers()),
    switchMap(layers => [
      new AddLayers(ActionTargetType.Home, layers),
      new AddLayers(ActionTargetType.Location, layers),
      new AddLayers(ActionTargetType.Project, layers),
      new AddLayers(ActionTargetType.Application, layers)
    ])
  )));
  
  initMapLayerTree: Observable<Action> = createEffect(() => defer(() => this.store.pipe(
    select(fromAuth.getLoggedIn),
    filter(loggedIn => loggedIn),
    switchMap(() => forkJoin([
      this.configService.isStagingOrProduction(), 
      this.user.hasRole(allowedRolesToSeeUndergroundAndCableLayers) 
    ])),
    switchMap(([isStagingOrProduction, userCanAccessUndergroundAndCableLayers]) => [
      new AddTreeStructure(ActionTargetType.Home, createLayerTree(isStagingOrProduction, true, userCanAccessUndergroundAndCableLayers)),
      new AddTreeStructure(ActionTargetType.Location, createLayerTree(isStagingOrProduction, true, userCanAccessUndergroundAndCableLayers)),
      new AddTreeStructure(ActionTargetType.Project, createLayerTree(isStagingOrProduction, true, userCanAccessUndergroundAndCableLayers)),
      new AddTreeStructure(ActionTargetType.Application, createLayerTree(isStagingOrProduction, false, userCanAccessUndergroundAndCableLayers))
    ])
  )));

  
  layersSelected: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SelectLayers>(MapLayerActionType.SelectLayers),
    tap((action: SelectLayers) => {
      if (action.targetType === ActionTargetType.Location) {
        this.mapStore.locationSearchFilterChange({layers: action.payload});
      } else if (action.targetType === ActionTargetType.Home) {
        this.mapStore.mapSearchFilterChange({layers: action.payload});
      }
    })
  ), {dispatch: false});

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
