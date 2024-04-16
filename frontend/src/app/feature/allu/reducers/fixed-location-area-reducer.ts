import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {FixedLocationArea} from '@model/common/fixed-location-area';
import {FixedLocationAreaActions, FixedLocationAreaActionType} from '@feature/allu/actions/fixed-location-area-actions';

export function sortAreas(left: FixedLocationArea, right: FixedLocationArea) {
  return left.name.localeCompare(right.name);
}

export interface State extends EntityState<FixedLocationArea> {
  selectedId: number;
}

export const adapter: EntityAdapter<FixedLocationArea> = createEntityAdapter<FixedLocationArea>({
  selectId: (area: FixedLocationArea) => area.id,
  sortComparer: sortAreas
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: FixedLocationAreaActions) {
  switch (action.type) {
    case FixedLocationAreaActionType.LoadSuccess: {
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
