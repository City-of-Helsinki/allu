import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {Feature, GeometryObject} from 'geojson';
import {UserAreaActions, UserAreaActionType} from '@feature/application/location/actions/user-area-actions';

export interface State extends EntityState<Feature<GeometryObject>> {
  selectedId: string;
  loading: boolean;
}

export const adapter: EntityAdapter<Feature<GeometryObject>> = createEntityAdapter<Feature<GeometryObject>>({
  selectId: (feature: Feature<GeometryObject>) => <string>feature.id
});

export const initialState: State = adapter.getInitialState({
  selectedId: undefined,
  loading: false
});

export function reducer(state: State = initialState, action: UserAreaActions) {
  switch (action.type) {
    case UserAreaActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case UserAreaActionType.LoadSuccess: {
      return adapter.addAll(action.payload.features, {
        ...state,
        loading: false
      });
    }

    case UserAreaActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;
