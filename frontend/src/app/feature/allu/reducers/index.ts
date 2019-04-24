import * as fromCityDistricts from './city-district-reducer';
import * as fromCodeSets from './code-set-reducer';
import * as fromConfigurations from './configuration-reducer';
import * as fromUsers from './user-reducer';
import * as fromFixedLocations from './fixed-location-reducer';
import * as fromFixedLocationAreas from './fixed-location-area-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Some} from '@util/option';
import {CodeSetTypeMap} from '@model/codeset/codeset';
import {User} from '@model/user/user';
import {Configuration} from '@model/config/configuration';
import {ApplicationKind} from '@model/application/type/application-kind';


export interface State {
  cityDistricts: fromCityDistricts.State;
  codeSets: fromCodeSets.State;
  configurations: fromConfigurations.State;
  users: fromUsers.State;
  fixedLocations: fromFixedLocations.State;
  fixedLocationAreas: fromFixedLocationAreas.State;
}

export const reducers: ActionReducerMap<State> = {
  cityDistricts: fromCityDistricts.reducer,
  codeSets: fromCodeSets.reducer,
  configurations: fromConfigurations.reducer,
  users: fromUsers.reducer,
  fixedLocations: fromFixedLocations.reducer,
  fixedLocationAreas: fromFixedLocationAreas.reducer
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

export const {
  selectIds: getConfigurationIds,
  selectEntities: getConfigurationEntities,
  selectAll: getAllConfigurations,
  selectTotal: getConfigurationsTotal
} = fromConfigurations.adapter.getSelectors(getConfigurationState);

export const getEditableConfigurations = createSelector(
  getAllConfigurations,
  (configurations: Configuration[]) => configurations.filter(c => c.editable)
);

export const getUsersState = createFeatureSelector<fromUsers.State>('users');

export const {
  selectIds: getUserIds,
  selectEntities: getUserEntities,
  selectAll: getAllUsers,
  selectTotal: getUsersTotal
} = fromUsers.adapter.getSelectors(getUsersState);

export const getActiveUsers = createSelector(
  getAllUsers,
  (users: User[]) => users.filter(user => user.isActive)
);

export const getFixedLocationsState = createFeatureSelector<fromFixedLocations.State>('fixedLocations');

export const {
  selectIds: getFixedLocationIds,
  selectEntities: getFixedLocationEntities,
  selectAll: getAllFixedLocations,
  selectTotal: getFixedLocationTotal
} = fromFixedLocations.adapter.getSelectors(getFixedLocationsState);

export const getFixedLocationById = (id: number) => createSelector(
  getFixedLocationEntities,
  (fixedLocations) => fixedLocations[id]
);

export const getFixedLocationsByIds = (ids: number[] = []) => createSelector(
  getFixedLocationEntities,
  (fixedLocations) => !!ids ? ids.map(id => fixedLocations[id]).filter(fl => !!fl) : []
);

export const getFixedLocationsByKind = (kind: ApplicationKind) => createSelector(
  getAllFixedLocations,
  (fixedLocations) => fixedLocations.filter(fl => fl.applicationKind === kind)
);

export const getFixedLocationAreaState = createFeatureSelector<fromFixedLocationAreas.State>('fixedLocationAreas');

export const {
  selectIds: getFixedLocationAreaIds,
  selectEntities: getFixedLocationAreaEntities,
  selectAll: getAllFixedLocationAreas,
  selectTotal: getFixedLocationAreaTotal
} = fromFixedLocationAreas.adapter.getSelectors(getFixedLocationAreaState);
