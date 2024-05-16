import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {FixedLocationActions, FixedLocationActionType} from '@feature/allu/actions/fixed-location-actions';
import {FixedLocation} from '@model/common/fixed-location';

export function sortFixedLocations(left: FixedLocation, right: FixedLocation) {
  return left.name.localeCompare(right.name);
}

export interface State extends EntityState<FixedLocation> {
  selectedId: number;
}

export const adapter: EntityAdapter<FixedLocation> = createEntityAdapter<FixedLocation>({
  selectId: (fixedLocation: FixedLocation) => fixedLocation.id,
  sortComparer: sortFixedLocations
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: FixedLocationActions) {
  switch (action.type) {
    case FixedLocationActionType.LoadSuccess: {
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
