import {InjectionToken} from '@angular/core';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {State} from '@feature/application/reducers';
import * as fromRoot from '@feature/allu/reducers';
import * as fromLayers from '@feature/map/reducers/map-layer-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {createMapLayerSelectors} from '@feature/map/reducers';

export interface LocationState {
  layers: fromLayers.State;
}

export interface State extends fromRoot.State {
  location: LocationState;
}

export const reducers: ActionReducerMap<LocationState> = {
  layers: fromLayers.createReducerFor(ActionTargetType.Location)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Location reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getLocationState = createFeatureSelector<LocationState>('location');

export const getMapLayersEntityState = createSelector(
  getLocationState,
  (state: LocationState) => state.layers
);

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
