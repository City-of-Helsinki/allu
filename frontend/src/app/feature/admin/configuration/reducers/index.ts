import * as fromConfigurations from '@feature/allu/reducers/configuration-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {InjectionToken} from '@angular/core';
import {Configuration} from '@model/config/configuration';

export interface State {
  configurations: fromConfigurations.State;
}

export const reducers: ActionReducerMap<State> = {
  configurations: fromConfigurations.reducer
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Configuration reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getConfigurationState = createFeatureSelector<State>('configuration');

export const getConfigurationEntitiesState = createSelector(
  getConfigurationState,
  state => state.configurations
);

export const {
  selectIds: getConfigurationIds,
  selectEntities: getConfigurationEntities,
  selectAll: getAllConfigurations,
  selectTotal: getConfigurationsTotal
} = fromConfigurations.adapter.getSelectors(getConfigurationEntitiesState);

export const getEditableConfigurations = createSelector(
  getAllConfigurations,
  (configurations: Configuration[]) => configurations.filter(c => c.editable)
);
