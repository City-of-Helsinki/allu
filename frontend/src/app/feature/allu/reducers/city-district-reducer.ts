import {CityDistrict} from '../../../model/common/city-district';
import {CityDistrictActions, CityDistrictActionType} from '../actions/city-district-actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';

export interface State extends EntityState<CityDistrict> {
  selectedId: number;
}

export const adapter: EntityAdapter<CityDistrict> = createEntityAdapter<CityDistrict>({
  selectId: (cityDistrict: CityDistrict) => cityDistrict.id
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: CityDistrictActions) {
  switch (action.type) {
    case CityDistrictActionType.LoadSuccess: {
      return adapter.setAll(action.payload, {
        ...state,
        selectedId: state.selectedId
      });
    }

    default: {
      return {...state};
    }
  }
}
