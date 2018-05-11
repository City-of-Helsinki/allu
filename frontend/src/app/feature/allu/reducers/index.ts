import * as fromCityDistricts from './city-district-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Some} from '../../../util/option';

export interface State {
  cityDistricts: fromCityDistricts.State;
}

export const reducers: ActionReducerMap<State> = {
  cityDistricts: fromCityDistricts.reducer
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
