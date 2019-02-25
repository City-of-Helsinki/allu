import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {FixedLocationArea} from '@model/common/fixed-location-area';
import {FixedLocationActions, FixedLocationActionType} from '@feature/allu/actions/fixed-location-actions';

export interface State extends EntityState<FixedLocationArea> {
  selectedId: number;
}

export const adapter: EntityAdapter<FixedLocationArea> = createEntityAdapter<FixedLocationArea>({
  selectId: (fixedLocationArea: FixedLocationArea) => fixedLocationArea.id
});

const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: FixedLocationActions) {
  switch (action.type) {
    case FixedLocationActionType.LoadSuccess: {
      return adapter.addAll(action.payload, {
        ...state,
        selectedId: state.selectedId
      });
    }

    default: {
      return {...state};
    }
  }
}
