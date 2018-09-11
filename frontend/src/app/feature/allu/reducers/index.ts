import * as fromCityDistricts from './city-district-reducer';
import * as fromCodeSets from './code-set-reducer';
import * as fromConfigurations from './configuration-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Some} from '../../../util/option';
import {CodeSetTypeMap} from '../../../model/codeset/codeset';
import {ConfigurationKeyMap} from '@model/config/configuration';

export interface State {
  cityDistricts: fromCityDistricts.State;
  codeSets: fromCodeSets.State;
  configurations: fromConfigurations.State;
}

export const reducers: ActionReducerMap<State> = {
  cityDistricts: fromCityDistricts.reducer,
  codeSets: fromCodeSets.reducer,
  configurations: fromConfigurations.reducer
};

export const getCityDistrictsState = createFeatureSelector<fromCityDistricts.State>('cityDistricts');

export const {
  selectIds: getCityDistrictIds,
  selectEntities: getCityDistrictEntities,
  selectAll: getAllCityDistricts,
  selectTotal: getCityDistrictTotal
} = fromCityDistricts.adapter.getSelectors(getCityDistrictsState);

export const getCityDistrictById = (id: number) => createSelector(
  getCityDistrictEntities,
  (districts) => id >= 0 ? districts[id] : undefined
);

export const getCityDistrictName = (id: number) => createSelector(
  getCityDistrictById(id),
  (district) => Some(district).map(d => d.name).orElse('')
);

export const getCityDistrictsByIds = (ids: number[]) => createSelector(
  getCityDistrictEntities,
  (districts) => ids.map(id => districts[id])
);

export const getCodeSetState = createFeatureSelector<fromCodeSets.State>('codeSets');

export const getCodeSetEntityState = createSelector(
  getCodeSetState,
  fromCodeSets.getCodeSet
);

export const getCodeSetCodeMap = (type: string) => createSelector(
  getCodeSetEntityState,
  (byTypes: CodeSetTypeMap) => Some(byTypes[type]).orElse({})
);

export const getConfigurationState = createFeatureSelector<fromConfigurations.State>('configurations');

export const getConfigurationEntityState = createSelector(
  getConfigurationState,
  fromConfigurations.getConfiguration
);

export const getConfiguration = (key: string) => createSelector(
  getConfigurationEntityState,
  (byKeys: ConfigurationKeyMap) => byKeys[key]
);
