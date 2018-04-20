import * as fromCityDistricts from './city-district-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';

export interface State {
  cityDistricts: fromCityDistricts.State;
}

export const reducers: ActionReducerMap<State> = {
  cityDistricts: fromCityDistricts.reducer
};

export const getCityDistrictsState = createFeatureSelector<fromCityDistricts.State>('cityDistricts');

export const getAllCityDistricts = createSelector(
  getCityDistrictsState,
  fromCityDistricts.getAllCityDistricts
);
