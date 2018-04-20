import {CityDistrict} from '../../../model/common/city-district';
import {CityDistrictActions, CityDistrictActionType} from '../actions/city-district-actions';

export interface State {
  cityDistricts: Map<number, CityDistrict>;
}

const initialState: State = {
  cityDistricts: new Map<number, CityDistrict>()
};

export function reducer(state: State = initialState, action: CityDistrictActions) {
  switch (action.type) {
    case CityDistrictActionType.LoadSuccess: {
      const districts = new Map<number, CityDistrict>();
      action.payload.forEach(d => districts.set(d.id, d));

      return {
        ...state,
        cityDistricts: districts
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getAllCityDistricts = (state: State) => state.cityDistricts;
