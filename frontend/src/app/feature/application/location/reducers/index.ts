import {InjectionToken} from '@angular/core';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromLayers from '@feature/map/reducers/map-layer-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {createMapLayerSelectors} from '@feature/map/reducers';
import * as fromApplicationSearch from '@feature/application/reducers/application-search-reducer';
import * as fromUserAreas from '@feature/application/location/reducers/user-area-reducer';

export interface LocationState {
  layers: fromLayers.State;
  applicationSearch: fromApplicationSearch.State;
  userAreas: fromUserAreas.State;
}

export interface State extends fromRoot.State {
  location: LocationState;
}

export const reducers: ActionReducerMap<LocationState> = {
  layers: fromLayers.createReducerFor(ActionTargetType.Location),
  applicationSearch: fromApplicationSearch.createReducerFor(ActionTargetType.Location),
  userAreas: fromUserAreas.reducer
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

// Application search selectors
export const getApplicationSearchState = createSelector(
  getLocationState,
  (state: LocationState) => state.applicationSearch
);

export const getMatchingApplications = createSelector(
  getApplicationSearchState,
  fromApplicationSearch.getMatchingApplicationsList
);

// User geometry selectors
export const getUserAreasState = createSelector(
  getLocationState,
  (state: LocationState) => state.userAreas
);

export const {
  selectIds: getUserAreaIds,
  selectEntities: getUserAreaEntities,
  selectAll: getAllUserAreas,
  selectTotal: getUserAreaCount,
} = fromUserAreas.adapter.getSelectors(getUserAreasState);

export const getUserAreasLoading = createSelector(
  getUserAreasState,
  fromUserAreas.getLoading
);
