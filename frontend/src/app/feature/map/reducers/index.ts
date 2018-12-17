import {InjectionToken} from '@angular/core';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {State} from '../../application/reducers';
import * as fromRoot from '../../allu/reducers';
import * as fromLayers from './map-layer-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Dictionary} from '@ngrx/entity';
import {MapLayer} from '@service/map/map-layer';

export interface MapState {
  layers: fromLayers.State;
}

export interface State extends fromRoot.State {
  map: MapState;
}

export const reducers: ActionReducerMap<MapState> = {
  layers: fromLayers.createReducerFor(ActionTargetType.Home)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Map reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getMapState = createFeatureSelector<MapState>('map');

export const getMapLayersEntityState = createSelector(
  getMapState,
  (state: MapState) => state.layers
);

export const getSelectedLayers = createSelector(
  getMapLayersEntityState,
  fromLayers.getSelected
);

export const {
  selectIds: getLayerIds,
  selectEntities: getLayerEntities,
  selectAll: getAllLayers,
  selectTotal: getLayersCount
} = fromLayers.adapter.getSelectors(getMapLayersEntityState);

export const getLayersByIds = (ids: string[] = []) => createSelector(
  getLayerEntities,
  (layers: Dictionary<MapLayer>) => ids.map(id => layers[id])
);

export const getTreeStructure = createSelector(
  getMapLayersEntityState,
  fromLayers.getTreeStructure
);
