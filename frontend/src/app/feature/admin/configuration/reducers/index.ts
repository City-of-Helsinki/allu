import * as fromConfigurations from '@feature/allu/reducers/configuration-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {InjectionToken} from '@angular/core';
import {Configuration} from '@model/config/configuration';
import * as fromContactSearch from '@feature/customerregistry/reducers/contact-search-reducer';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import { ConfigurationKey } from '@app/model/config/configuration-key';

export interface State {
  configurations: fromConfigurations.State;
  contactSearch: fromContactSearch.State;
}

export const reducers: ActionReducerMap<State> = {
  configurations: fromConfigurations.reducer,
  contactSearch: fromContactSearch.createReducerFor(ActionTargetType.Configuration)
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

export const getConfiguration = (key: ConfigurationKey) => createSelector(
  getAllConfigurations,
  (configurations: Configuration[]) => configurations.filter(c => c.key === key)
);

export const getConfigurationContactSearchState = createSelector(
  getConfigurationState,
  state => state.contactSearch
);

export const {
  getMatching: getMatchingContacts
} = fromContactSearch.createContactSelectors(getConfigurationContactSearchState);
