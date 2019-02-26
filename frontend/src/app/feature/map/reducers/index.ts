import {InjectionToken} from '@angular/core';
import {ActionReducerMap, createFeatureSelector, createSelector, MemoizedSelector} from '@ngrx/store';
import {State} from '../../application/reducers';
import * as fromRoot from '../../allu/reducers';
import * as fromLayers from './map-layer-reducer';
import * as fromAddressSearch from './address-search-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Dictionary} from '@ngrx/entity';
import {MapLayer} from '@service/map/map-layer';

export interface MapState {
  layers: fromLayers.State;
  address: fromAddressSearch.State;
}

export interface State extends fromRoot.State {
  map: MapState;
}

export const reducers: ActionReducerMap<MapState> = {
  layers: fromLayers.createReducerFor(ActionTargetType.Home),
  address: fromAddressSearch.reducer
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Map reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getMapState = createFeatureSelector<MapState>('map');


/**
 * Map layer selectors
 */
export const getMapLayersEntityState = createSelector(
  getMapState,
  (state: MapState) => state.layers
);

export const createIdSelector = (getState: MemoizedSelector<object, fromLayers.State>) => createSelector(
  getState,
  fromLayers.getSelected
);

export function createMapLayerSelectors(getState: MemoizedSelector<object, fromLayers.State>) {
  const entitySelectors = fromLayers.adapter.getSelectors(getState);
  const selectIds = createIdSelector(getState);

  return {
    ...entitySelectors,
    getSelectedLayerIds: selectIds,
    getSelectedLayers: createSelector(
      selectIds,
      entitySelectors.selectEntities,
      (ids: string[] = [], layers: Dictionary<MapLayer>) => ids.map(id => layers[id])
    ),
    getTreeStructure: createSelector(
      getState,
      fromLayers.getTreeStructure
    )
  };
}

export const {
  selectIds: getLayerIds,
  selectEntities: getLayerEntities,
  selectAll: getAllLayers,
  selectTotal: getLayersCount,
  getSelectedLayerIds: getSelectedLayerIds,
  getSelectedLayers,
  getTreeStructure
} = createMapLayerSelectors(getMapLayersEntityState);


/**
 * Address search reducers
 */
export const getAddressSearchEntityState = createSelector(
  getMapState,
  (state: MapState) => state.address
);

export const getMatchingAddressed = createSelector(
  getAddressSearchEntityState,
  fromAddressSearch.getMatching
);

export const getCoordinates = createSelector(
  getAddressSearchEntityState,
  fromAddressSearch.getCoordinates
);
