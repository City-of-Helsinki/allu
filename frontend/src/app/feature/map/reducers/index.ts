import {InjectionToken} from '@angular/core';
import {ActionReducerMap, createFeatureSelector, createSelector, MemoizedSelector} from '@ngrx/store';
import * as fromRoot from '../../allu/reducers';
import * as fromLayers from './map-layer-reducer';
import * as fromAddressSearch from './address-search-reducer';
import * as fromApplication from './application-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Dictionary} from '@ngrx/entity';
import {MapLayer} from '@service/map/map-layer';
import {ApplicationType, applicationTypeList} from '@model/application/type/application-type';
import {ArrayUtil} from '@util/array-util';

export interface MapState {
  layers: fromLayers.State;
  address: fromAddressSearch.State;
  applications: fromApplication.State;
}

export interface State extends fromRoot.State {
  map: MapState;
}

export const reducers: ActionReducerMap<MapState> = {
  layers: fromLayers.createReducerFor(ActionTargetType.Home),
  address: fromAddressSearch.reducer,
  applications: fromApplication.reducer
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

const getLayers = (ids: string[] = [], layers: Dictionary<MapLayer>) => ids.map(id => layers[id]);

const getApplicationLayers = (ids: string[] = [], layers: Dictionary<MapLayer>) => getLayers(ids, layers)
  .filter(layer => layer && layer.applicationType)
  .filter(layer => applicationTypeList.indexOf(layer.applicationType) >= 0);

export function createMapLayerSelectors(getState: MemoizedSelector<object, fromLayers.State>) {
  const entitySelectors = fromLayers.adapter.getSelectors(getState);
  const selectIds = createIdSelector(getState);

  return {
    ...entitySelectors,
    getSelectedLayerIds: selectIds,
    getSelectedLayers: createSelector(
      selectIds,
      entitySelectors.selectEntities,
      getLayers
    ),
    getTreeStructure: createSelector(
      getState,
      fromLayers.getTreeStructure
    ),
    getSelectedApplicationLayers: createSelector(
      selectIds,
      entitySelectors.selectEntities,
      getApplicationLayers
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
  getTreeStructure,
  getSelectedApplicationLayers
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

/**
 * Application reducers
 */

export const getApplicationEntityState = createSelector(
  getMapState,
  (state: MapState) => state.applications
);

export const getApplicationsLoading = createSelector(
  getApplicationEntityState,
  fromApplication.getLoading
);

export const getApplications = createSelector(
  getApplicationEntityState,
  fromApplication.getApplications
);
