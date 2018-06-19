import * as fromCityDistricts from './city-district-reducer';
import * as fromCodeSets from './code-set-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Some} from '../../../util/option';
import {CodeSetTypeMap} from '../../../model/codeset/codeset';

export interface State {
  cityDistricts: fromCityDistricts.State;
  codeSets: fromCodeSets.State;
}

export const reducers: ActionReducerMap<State> = {
  cityDistricts: fromCityDistricts.reducer,
  codeSets: fromCodeSets.reducer
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
